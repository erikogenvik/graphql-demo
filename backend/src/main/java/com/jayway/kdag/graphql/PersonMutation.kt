package com.jayway.kdag.graphql

import graphql.annotations.GraphQLField

interface PersonMutation {

    @GraphQLField
    fun createPerson(firstName: String, lastName: String): Person?

    @GraphQLField
    fun deletePerson(id: String): Boolean
}
