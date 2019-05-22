package com.jayway.kdag.graphql


data class ApolloRequestBody(
        val query: String?,
        val operationName: String?,
        val variables: Map<String, Any>?
)