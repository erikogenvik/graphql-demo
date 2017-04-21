package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLField;

import java.util.List;

/**
 * Created by erik on 2017-04-21.
 */
public interface PersonQuery {

    @GraphQLField
    List<Person> people();
}
