package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLAnnotations;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import static graphql.schema.GraphQLSchema.newSchema;

class Schema {

    final GraphQLSchema mainSchema;

    Schema() throws IllegalAccessException, NoSuchMethodException, InstantiationException {

        GraphQLAnnotations.object(Person.class);
        GraphQLObjectType personQuery = GraphQLAnnotations.object(PersonQuery.class);
        GraphQLObjectType personMutation = GraphQLAnnotations.object(PersonMutation.class);
        mainSchema = newSchema().query(personQuery).mutation(personMutation).build();
    }

}
