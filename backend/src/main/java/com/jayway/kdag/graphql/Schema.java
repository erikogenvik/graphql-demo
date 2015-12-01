package com.jayway.kdag.graphql;

import graphql.Scalars;
import graphql.schema.*;

import java.util.List;
import java.util.UUID;

import static graphql.schema.GraphQLArgument.newArgument;
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

    public static GraphQLObjectType MutationType = newObject()
            .name("PersonMutation")
            .field(newFieldDefinition()
                    .name("createPerson")
                    .type(PersonType)
                    .argument(newArgument().name("firstName").type(Scalars.GraphQLString).build())
                    .argument(newArgument().name("lastName").type(Scalars.GraphQLString).build())
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment environment) {
                            List<Person> people = (List<Person>) environment.getContext();
                            Person person = new Person();
                            person.firstName = environment.getArgument("firstName");
                            person.lastName = environment.getArgument("lastName");
                            person.id = UUID.randomUUID().toString();
                            people.add(person);
                            return person;
                        }
                    })
                    .build())
            .build();
    public static GraphQLSchema MainSchema = newSchema().query(QueryType).mutation(MutationType).build();
}
