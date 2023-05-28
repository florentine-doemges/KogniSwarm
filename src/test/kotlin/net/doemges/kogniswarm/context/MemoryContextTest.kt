package net.doemges.kogniswarm.context

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import io.mockk.every
import io.mockk.mockk
import io.weaviate.client.base.Result
import io.weaviate.client.v1.data.Data
import io.weaviate.client.v1.data.api.ObjectCreator
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.graphql.GraphQL
import io.weaviate.client.v1.graphql.model.GraphQLResponse
import io.weaviate.client.v1.graphql.query.Get
import io.weaviate.client.v1.misc.Misc
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.Mission
import net.doemges.kogniswarm.tool.Tool
import net.doemges.kogniswarm.weaviate.TestableWeaviateClient
import org.apache.camel.Exchange
import org.junit.jupiter.api.Test

class MemoryContextTest {
    @Test
    fun testPutAndGet() {
        val context = MemoryContext(object : TestableWeaviateClient {
            override fun data(): Data = mockk<Data>().apply {
                every { creator() } returns mockk<ObjectCreator>().apply {
                    every { withClassName(any()) } returns this
                    every { withID(any()) } returns this
                    every { withProperties(any()) } returns this
                    every { run() } returns mockk<Result<WeaviateObject>>().apply {
                        every { hasErrors() } returns false
                    }
                }
            }

            override fun graphQL(): GraphQL {
                val getResultsMap = mockk<Map<String, Any?>>()
                val memoryResultsMap = mockk<Map<String, Any?>>()
                every { getResultsMap[any()] } returns memoryResultsMap
                every { memoryResultsMap[any()] } returns ArrayList<Map<String, String>>()
                return mockk<GraphQL>().apply {
                    every { get() } returns mockk<Get>().apply {
                        every { withClassName(any()) } returns this
                        every { withNearText(any()) } returns this
                        every { withLimit(any()) } returns this
                        every { withFields(*anyVararg()) } returns this
                        every { run() } returns mockk<Result<GraphQLResponse>>().apply {
                            every { result } returns mockk<GraphQLResponse>().apply {
                                every { data } returns getResultsMap
                            }
                        }
                    }
                }
            }

            override fun misc(): Misc = mockk()

        })
        val action = Action(object : Tool {
            override val name: String = "test"
            override val description: String = "test"
            override val args: Map<String, String> = emptyMap()

            override fun process(exchange: Exchange?) {
                TODO("Not yet implemented")
            }
        }, emptyMap())

        assertThat {
            context.put(
                Mission(
                    "user",
                    "agentName",
                    "userPrompt"
                ), action
            )
        }.isSuccess()

        assertThat { context.get(Mission("user", "agentName", "userPrompt")) }.isSuccess()
            .isEqualTo("")
    }
}