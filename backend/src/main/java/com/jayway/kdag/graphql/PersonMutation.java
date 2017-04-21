package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLField;

/**
 * Created by erik on 2017-04-21.
 */
public interface PersonMutation {

    @GraphQLField
    Person createPerson(String firstName, String lastName);
}
