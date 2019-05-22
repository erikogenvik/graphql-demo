package com.jayway.kdag.graphql


import com.couchbase.lite.CouchbaseLiteException
import graphql.ExecutionInput
import graphql.GraphQL
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.io.IOException


@RestController
@RequestMapping(value = ["/graphql"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@CrossOrigin(origins = ["*"])
class GraphQLResource @Throws(IllegalAccessException::class, NoSuchMethodException::class, InstantiationException::class, IOException::class, CouchbaseLiteException::class)
constructor(val graphQL: GraphQL,
            val persons: Persons) {

    @RequestMapping(method = [RequestMethod.POST])
    internal fun query(@RequestBody body: String): Mono<Map<String, Any>> {
        return executeGraphqlQuery(body, null, null)
    }

    @RequestMapping(path = ["apollo"], method = [RequestMethod.POST])
    internal fun apollo(@RequestBody body: ApolloRequestBody): Mono<Map<String, Any>> {
        return executeGraphqlQuery(body.query, body.operationName, body.variables)

    }

    private fun executeGraphqlQuery(query: String?, operationName: String?, variables: Map<String, Any>?): Mono<Map<String, Any>> {
        val executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .operationName(operationName)
                .variables(variables)
                .context(persons)
                .build()
        return Mono.fromCompletionStage(graphQL.executeAsync(executionInput).thenApply { it.toSpecification() })
    }

    companion object : KLogging()

}
