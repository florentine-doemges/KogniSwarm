package net.doemges.kogniswarm.weaviate.util

import io.weaviate.client.v1.data.Data
import io.weaviate.client.v1.graphql.GraphQL
import io.weaviate.client.v1.misc.Misc

interface TestableWeaviateClient{
    fun data(): Data
    fun graphQL(): GraphQL
    fun misc(): Misc

}