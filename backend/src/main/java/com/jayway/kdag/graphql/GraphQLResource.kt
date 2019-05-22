package com.jayway.kdag.graphql


import graphql.ExecutionInput
import graphql.GraphQL
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping(value = ["/graphql"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE],
        consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@CrossOrigin(origins = ["*"])
class GraphQLResource
constructor(val graphQL: GraphQL) {

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
                .build()
        return Mono.fromCompletionStage(graphQL.executeAsync(executionInput).thenApply { it.toSpecification() })
    }

//    @RequestMapping(method = [RequestMethod.POST])
//    internal fun simple(@RequestBody body: String): Map<String, Any> {
//        val executionInput = ExecutionInput.newExecutionInput()
//                .query(body)
//                .build()
//        return graphQL.execute(executionInput).toSpecification()
//    }
//
//    @RequestMapping(method = [RequestMethod.POST])
//    internal fun deferred(@RequestBody body: String): Mono<MutableMap<String, Any>> {
//        val executionInput = ExecutionInput.newExecutionInput()
//                .query(body)
//                .build()
//        return Mono.fromCompletionStage(graphQL
//                .executeAsync(executionInput)
//                .thenApply {
//                    it.toSpecification()
//                })
//    }


    companion object : KLogging()

}
