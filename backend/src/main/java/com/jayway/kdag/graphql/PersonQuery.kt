package com.jayway.kdag.graphql

import graphql.annotations.GraphQLField

interface PersonQuery {

    @GraphQLField
    fun people(): List<Person>?
}
