package com.jayway.kdag.graphql.socket

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import mu.KLogging
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.*

@Component
class GraphQLWebSocketHandler(private val graphQL: GraphQL, private val objectMapper: ObjectMapper) : TextWebSocketHandler() {

    internal var subscriptions: MutableMap<String, MutableMap<String, Subscription>> = HashMap()

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("Websocket connection established")
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("Closing subscription ")
        subscriptions[session.id]?.let {
            it.forEach { (_, value) -> value.cancel() }
            subscriptions.remove(session.id)
        }
    }

    @Throws(Exception::class)
    override fun handleTextMessage(webSocketSession: WebSocketSession, message: TextMessage) {

        val graphqlQuery = message.payload
        logger.info("Websocket said {}", graphqlQuery)

        val body = objectMapper.readValue<WebSocketRequestBody>(graphqlQuery, WebSocketRequestBody::class.java)

        if ("connection_init" == body.type) {
            val response = WebSocketResponseBody(type = MESSAGE_ACK)

            val json = objectMapper.writeValueAsString(response)

            webSocketSession.sendMessage(TextMessage(json))
            return
        }

        if ("stop" == body.type) {
            val response = WebSocketResponseBody(id = body.id, type = MESSAGE_COMPLETE)

            val json = objectMapper.writeValueAsString(response)

            webSocketSession.sendMessage(TextMessage(json))

            subscriptions[webSocketSession.id]?.let { innerMap ->
                innerMap[body.id]?.let { subscription ->
                    subscription.cancel()
                    innerMap.remove(body.id)
                    return
                }
            }

            return
        }

        if (body.payload == null) {
            return
        }

        val request = body.payload
        val executionInput = ExecutionInput.newExecutionInput()
                .query(request.query)
                .variables(request.variables)
                .operationName(request.operationName)
                .build()

        this.graphQL.executeAsync(executionInput).thenAccept { executionResult ->
            val spec = executionResult.toSpecification()
            val result = executionResult.getData<Any>()
            if (result is Publisher<*>) {
                val publisher = result as Publisher<ExecutionResult>;
                val id = body.id ?: throw RuntimeException("Request had no 'id' field, as required.")
                val subscriber = object : Subscriber<ExecutionResult> {
                    override fun onSubscribe(s: Subscription) {
                        s.request(java.lang.Long.MAX_VALUE)

                        val subscriptionsMap = subscriptions.getOrDefault(webSocketSession.id, HashMap())
                        subscriptionsMap[id]?.cancel()
                        subscriptionsMap.put(id, s)
                    }

                    override fun onNext(executionResult: ExecutionResult) {
                        try {

                            val response = WebSocketResponseBody(id = id, type = MESSAGE_DATA, payload = executionResult.toSpecification())

                            val json = objectMapper.writeValueAsString(response)

                            webSocketSession.sendMessage(TextMessage(json))
                        } catch (e: IOException) {
                            logger.error("Error when publishing.", e)
                        }

                    }

                    override fun onError(t: Throwable) {
                        logger.error("Error in subscribe stream.", t)
                    }

                    override fun onComplete() {}
                }


                publisher.subscribe(subscriber)

                //If the "data" is a publisher we can't serialize it, so we'll set that field to an empty string.
                spec["data"] = ""
            }



            try {
                val response = WebSocketResponseBody(id = body.id, type = MESSAGE_DATA, payload = spec)

                val json = objectMapper.writeValueAsString(response)
                webSocketSession.sendMessage(TextMessage(json))
            } catch (e: IOException) {
                logger.error("Error when handling web socket request.", e)
            }


        }

    }

    companion object : KLogging() {
        private const val MESSAGE_ACK = "connection_ack"
        private const val MESSAGE_COMPLETE = "complete"
        private const val MESSAGE_DATA = "data"
    }

}
