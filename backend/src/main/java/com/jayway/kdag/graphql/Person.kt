package com.jayway.kdag.graphql

import graphql.annotations.GraphQLField

/**
 * Created by erik on 2015-11-29.
 */
class Person {

    @GraphQLField
    var id: String? = null
    @GraphQLField
    var firstName: String? = null
    @GraphQLField
    var lastName: String? = null

}
