package com.jayway.kdag.graphql


interface PersonQuery {

    fun people(): List<Person>?
}
