package com.jayway.kdag.graphql

import graphql.annotations.GraphQLAnnotations
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema

import graphql.schema.GraphQLSchema.newSchema

internal class Schema @Throws(IllegalAccessException::class, NoSuchMethodException::class, InstantiationException::class)
constructor() {

    val mainSchema: GraphQLSchema

    init {

        GraphQLAnnotations.`object`(Person::class.java)
        val personQuery = GraphQLAnnotations.`object`(PersonQuery::class.java)
        val personMutation = GraphQLAnnotations.`object`(PersonMutation::class.java)
        mainSchema = newSchema().query(personQuery).mutation(personMutation).build()
    }

}
