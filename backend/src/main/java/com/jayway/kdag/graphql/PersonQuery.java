package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLField;

import java.util.List;

public interface PersonQuery {

    @GraphQLField
    List<Person> people();
}
