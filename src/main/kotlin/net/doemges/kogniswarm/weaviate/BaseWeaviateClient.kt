package net.doemges.kogniswarm.weaviate

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.data.Data
import io.weaviate.client.v1.graphql.GraphQL
import io.weaviate.client.v1.misc.Misc

class BaseWeaviateClient(private val weaviateClient: WeaviateClient) : TestableWeaviateClient {
    override fun data(): Data = weaviateClient.data()
    override fun graphQL(): GraphQL = weaviateClient.graphQL()
    override fun misc(): Misc = weaviateClient.misc()

}

