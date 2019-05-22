package com.jayway.kdag.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.TypeRuntimeWiring
import mu.KLogging
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Type
import kotlin.reflect.*
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaType


class FunctionDataFetcher(private val provider: Any,
                          private val member: KCallable<*>,
                          private val resolvers: Map<Type, (DataFetchingEnvironment) -> Any?> = mapOf()) : DataFetcher<Any> {
    override fun get(environment: DataFetchingEnvironment): Any? {
        val source = environment.getSource<Any>()

        val params = mutableMapOf<KParameter, Any?>()
        params[member.parameters.first()] = provider
        member.parameters.takeLast(member.parameters.size - 1).forEach { parameter ->
            try {
                val paramKlass = parameter.type.javaType

                //First check if the "source" matches the type of the parameter.
                if (source != null && parameter.type.classifier is KClass<*> && source.javaClass.kotlin.isSubclassOf(parameter.type.classifier as KClass<*>)) {
                    params[parameter] = source
                } else {
                    //Then check if there's any resolver which can extract the parameter value from the DataFetchingEnvironment.
                    val resolver = resolvers[paramKlass]

                    val param: Any? = if (resolver != null) resolver(environment) else environment.getArgument(parameter.name)
                    if (!parameter.type.isMarkedNullable && param == null) {
                        throw java.lang.RuntimeException("Could not find required parameter '${parameter.name}' of type '${parameter.type}' in function '$member'")
                    }

                    //Check if it's an enum; if so we need to resolve it from the string
                    if (paramKlass is Class<*> && paramKlass.isEnum) {
                        val enums = paramKlass.enumConstants as Array<Enum<*>>
                        params[parameter] = enums.first { it.name == param }
                    } else {
                        params[parameter] = param
                    }
                }
            } catch (ex: RuntimeException) {
                logger.error("Error when handling parameter '${parameter.name}' for provider $provider.", ex)
                throw ex
            }
        }
        try {
            return member.callBy(params)
        } catch (ex: InvocationTargetException) {
            logger.debug("Error when calling graphql endpoint ${member.name}", ex.targetException)
            throw ex.targetException
        } catch (ex: RuntimeException) {
            logger.debug("Error when calling graphql endpoint ${member.name}", ex)
            throw ex
        }
    }

    companion object : KLogging() {

        fun enhanceTypeWiring(typeWiring: TypeRuntimeWiring, provider: Any, resolvers: Map<Type, (DataFetchingEnvironment) -> Any?> = mapOf()): TypeRuntimeWiring {
            val klass = provider.javaClass.kotlin
            val members = klass.declaredMembers
            members
                    .filter { it.visibility == KVisibility.PUBLIC && it is KFunction }
                    .forEach { member ->
                        if (typeWiring.fieldDataFetchers.containsKey(member.name)) {
                            logger.warn { "Type wiring '${typeWiring.typeName}' already contains fetcher for the value ${member.name}." }
                        }
                        typeWiring.fieldDataFetchers[member.name] = FunctionDataFetcher(provider, member, resolvers)
                        logger.info { "Adding function data fetcher for the field ${member.name} to type wiring '${typeWiring.typeName}'" }
                    }

            return typeWiring
        }
    }

}

fun TypeRuntimeWiring.addFunctions(provider: Any, resolvers: Map<Type, (DataFetchingEnvironment) -> Any?> = mapOf()): TypeRuntimeWiring {
    return FunctionDataFetcher.enhanceTypeWiring(this, provider, resolvers)
}

fun TypeRuntimeWiring.Builder.functionFetcher(provider: Any, member: KCallable<*>, resolvers: Map<Type, (DataFetchingEnvironment) -> Any?> = mapOf()): TypeRuntimeWiring.Builder {
    this.dataFetcher(member.name, FunctionDataFetcher(provider, member, resolvers))
    return this
}