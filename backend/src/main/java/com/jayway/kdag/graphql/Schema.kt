package com.jayway.kdag.graphql

import graphql.GraphQL
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.schema.DataFetchingEnvironment
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
        val reader = InputStreamReader(sdl)
        val typeRegistry = SchemaParser().parse(reader)
        val runtimeWiring = buildWiring()
        val schemaGenerator = SchemaGenerator()
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)
    }

    private fun buildWiring(): RuntimeWiring {


        val resolvers: Map<Type, (environment: DataFetchingEnvironment) -> Any?> = mapOf(
                DataFetchingEnvironment::class.java to { environment: DataFetchingEnvironment ->
                    environment
                }
        )

        return RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .functionFetcher(persons, Persons::people, resolvers)
//                        .dataFetcher("hello", graphQLDataFetchers.helloWorldDataFetcher)
//                        .dataFetcher("echo", graphQLDataFetchers.echoDataFetcher)
                        .build())
                .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                        .functionFetcher(persons, Persons::deletePerson, resolvers)
                        .functionFetcher(persons, Persons::createPerson, resolvers)
                        .build())
                .type(TypeRuntimeWiring.newTypeWiring("Person")
                        .build())
                .build()

    }

    companion object : KLogging()
}
