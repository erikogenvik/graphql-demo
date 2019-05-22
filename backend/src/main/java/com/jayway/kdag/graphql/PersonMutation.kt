package com.jayway.kdag.graphql


interface PersonMutation {

    fun createPerson(firstName: String, lastName: String): Person?

    fun deletePerson(id: String): Boolean
}
