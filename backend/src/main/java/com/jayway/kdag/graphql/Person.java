package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLField;

/**
 * Created by erik on 2015-11-29.
 */
class Person {

    @GraphQLField
    String id;
    @GraphQLField
    String firstName;
    @GraphQLField
    String lastName;

}
