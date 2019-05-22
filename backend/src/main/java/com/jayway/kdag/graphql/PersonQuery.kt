package com.jayway.kdag.graphql

import graphql.annotations.annotationTypes.GraphQLField


interface PersonQuery {
    @GraphQLField
    fun people(): List<Person>?
}
