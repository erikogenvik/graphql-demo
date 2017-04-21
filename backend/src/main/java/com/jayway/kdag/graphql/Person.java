package com.jayway.kdag.graphql;

import graphql.annotations.GraphQLField;

/**
 * Created by erik on 2015-11-29.
 */
public class Person {

    @GraphQLField
    public String id;
    @GraphQLField
    public String firstName;
    @GraphQLField
    public String lastName;

}
