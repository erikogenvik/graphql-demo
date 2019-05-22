package com.jayway.kdag.graphql

import graphql.GraphQL
import graphql.annotations.processor.GraphQLAnnotations
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type


@Component
class Schema @Throws(IllegalAccessException::class, NoSuchMethodException::class, InstantiationException::class)
constructor(val persons: Persons) {


    @get:Bean
    val graphQL: GraphQL by lazy {
        val stream = ClassLoader.getSystemResourceAsStream("schema.graphqls")
                ?: throw RuntimeException("Could not load schema.")
        val graphQLSchema = buildSchema(stream)

        val instrumentation = ChainedInstrumentation(
                listOf(TracingInstrumentation())
        )

        GraphQL.newGraphQL(graphQLSchema)
                .instrumentation(instrumentation)
                .build()
    }

    private fun buildSchema(sdl: InputStream): GraphQLSchema {
        InputStreamReader(sdl).use {
            val typeRegistry = SchemaParser().parse(it)
            val runtimeWiring = buildWiring()
            val schemaGenerator = SchemaGenerator()
            return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)
        }
    }

    private fun buildWiring(): RuntimeWiring {

        val resolvers: Map<Type, (environment: DataFetchingEnvironment) -> Any?> = mapOf(
                DataFetchingEnvironment::class.java to { environment: DataFetchingEnvironment ->
                    environment
                }
        )

        val personFetcher = DataFetcher<List<Person>> { persons.people() }

        return RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Person")
                        .build())
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("persons", personFetcher)
                        .functionFetcher(persons, Persons::people, resolvers)
                        .build())
                .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                        .functionFetcher(persons, Persons::deletePerson, resolvers)
                        .functionFetcher(persons, Persons::createPerson, resolvers)
                        .build())
                .type(TypeRuntimeWiring.newTypeWiring("Subscription")
                        .functionFetcher(persons, Persons::subscribeNew, resolvers)
                        .build())
                .build()
    }


    private fun createSchema(sdl: InputStream): GraphQL? {
        InputStreamReader(sdl).use {
            val personFetcher = DataFetcher<List<Person>> { persons.people() }
            val wiring = RuntimeWiring.newRuntimeWiring()
                    .type(TypeRuntimeWiring.newTypeWiring("Query")
                            .dataFetcher("persons", personFetcher)
                            .build())
                    .type(TypeRuntimeWiring.newTypeWiring("Person")
                            .build())
                    .build()

            val schema = SchemaGenerator().makeExecutableSchema(SchemaParser().parse(it), wiring)
            return GraphQL.newGraphQL(schema).build()
        }
    }

    private fun buildSchema(): GraphQL? {

        val personFetcher = DataFetcher<List<Person>> { persons.people() }
        val personType = newObject().name("Person").build()
        val queryType = newObject()
                .name("Query")
                .field(newFieldDefinition()
                        .name("persons")
                        .type(GraphQLList.list(personType))
                        .dataFetcher(personFetcher)
                )
                .build()
        val schema = GraphQLSchema.newSchema().query(queryType).build()
        return GraphQL.newGraphQL(schema).build()
    }

    private fun buildSchemaWithAnnotations(): GraphQL? {
        val graphqlAnnotations = GraphQLAnnotations()
        graphqlAnnotations.`object`(Person::class.java)
        val personQuery = graphqlAnnotations.`object`(PersonQuery::class.java)
        val personMutation = graphqlAnnotations.`object`(PersonMutation::class.java)
        val schema = GraphQLSchema.newSchema().query(personQuery).mutation(personMutation).build()
        return GraphQL.newGraphQL(schema).build()

    }

    companion object : KLogging()
}
