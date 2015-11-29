package com.jayway.kdag.graphql;

import graphql.Scalars;
import graphql.schema.*;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

/**
 * Created by erik on 2015-11-29.
 */
public class Schema {
    public static GraphQLObjectType PersonType = newObject()
            .name("Person")
            .field(newFieldDefinition()
                    .type(Scalars.GraphQLString)
                    .name("firstName")
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .type(Scalars.GraphQLString)
                    .name("lastName")
                    .fetchField()
                    .build())
            .build();

    public static GraphQLObjectType QueryType = newObject()
            .name("PersonQuery")
            .field(newFieldDefinition()
                    .name("people")
                    .type(new GraphQLList(PersonType))
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment environment) {
                            return environment.getContext();
                        }
                    })
                    .build())
            .build();

    public static GraphQLSchema MainSchema = newSchema().query(QueryType).build();
}
