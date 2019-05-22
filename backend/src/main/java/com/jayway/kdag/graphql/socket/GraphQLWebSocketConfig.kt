package com.jayway.kdag.graphql.socket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocket
class GraphQLWebSocketConfig @Autowired
constructor(private val handler: GraphQLWebSocketHandler) : WebSocketConfigurer {

    @Bean
    fun createWebSocketContainer(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.maxTextMessageBufferSize = 8192
        container.maxBinaryMessageBufferSize = 8192
        return container
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        val handshakeHandler = DefaultHandshakeHandler()
        handshakeHandler.setSupportedProtocols("graphql-ws")

        registry.addHandler(handler, "/ws/graphql")
                .setAllowedOrigins("*")
                .setHandshakeHandler(handshakeHandler)
    }
}
