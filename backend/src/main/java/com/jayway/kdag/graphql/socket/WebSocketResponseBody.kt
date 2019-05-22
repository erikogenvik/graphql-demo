package com.jayway.kdag.graphql.socket

data class WebSocketResponseBody(
        val id: String? = null,
        val type: String? = null,
        val payload: Any? = null
)
