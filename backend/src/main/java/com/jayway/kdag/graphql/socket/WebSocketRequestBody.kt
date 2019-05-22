package com.jayway.kdag.graphql.socket

import com.jayway.kdag.graphql.ApolloRequestBody

data class WebSocketRequestBody(
        val id: String?,
        val type: String?,
        val payload: ApolloRequestBody?
)
