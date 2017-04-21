package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLField;

public interface PersonMutation {

    @GraphQLField
    Person createPerson(String firstName, String lastName);

    @GraphQLField
    boolean deletePerson(String id);
}
