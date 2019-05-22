package com.jayway.kdag.graphql

import graphql.annotations.annotationTypes.GraphQLField

data class Person(
        @GraphQLField
        val id: String,
        @GraphQLField
        val firstName: String,
        @GraphQLField
        val lastName: String
)
