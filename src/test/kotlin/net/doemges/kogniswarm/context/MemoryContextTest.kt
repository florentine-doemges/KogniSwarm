package net.doemges.kogniswarm.context

import assertk.all
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import io.mockk.every
import io.mockk.mockk
import io.weaviate.client.base.Result
import io.weaviate.client.base.WeaviateError
import io.weaviate.client.base.WeaviateErrorMessage
import io.weaviate.client.v1.data.Data
import io.weaviate.client.v1.data.api.ObjectCreator
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.graphql.GraphQL
import io.weaviate.client.v1.graphql.model.GraphQLResponse
import io.weaviate.client.v1.graphql.query.Get
import io.weaviate.client.v1.misc.Misc
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.context.service.MemoryContextService
import net.doemges.kogniswarm.mission.model.MissionKey
import net.doemges.kogniswarm.token.util.Tokenizer
import net.doemges.kogniswarm.token.service.TokenizerService
import net.doemges.kogniswarm.tool.processor.ToolProcessor
import net.doemges.kogniswarm.weaviate.util.TestableWeaviateClient
import org.apache.camel.Exchange
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MemoryContextTest {
    private lateinit var memoryContextService: MemoryContextService

    @BeforeEach
    fun setup() {
        val testClient = createTestableWeaviateClient(
            memoryResults = ArrayList<Map<String, String>>().apply {
                add(
                    mapOf(
                        "userName" to "user",
                        "agentName" to "agentName",
                        "userPrompt" to "userPrompt",
                        "toolName" to "toolName",
                        "toolDescription" to "toolDescription",
                        "args" to "arg1=value1, arg2=value2",
                        "result" to "result",
                        "description" to "description"
                    )
                )
            }

        )
        val tokenizerService = mockk<TokenizerService>().apply {
            every { tokenizer } returns mockk<Tokenizer>().apply {
                every { tokenize(any()) } returns listOf("action")
            }
        }
        memoryContextService = MemoryContextService(testClient, tokenizerService, mockk())
    }

    @Test
    fun testPutAndGet() {
        val action = createAction()

        runAssertions {
            memoryContextService.put(createMission(), action)
        }

        val result = runAssertions {
            memoryContextService.get(createMission())
        }

        assertThat(result).all{
            isNotNull()
            isInstanceOf(ArrayList::class)
            isNotEmpty()
        }
    }

    @Test
    fun testPutThrowsError() {
        val testClient = createTestableWeaviateClient(hasErrors = true, memoryResults = ArrayList())
        memoryContextService = MemoryContextService(testClient, mockk(), mockk())
        val action = createAction()

        assertThrows(RuntimeException::class.java) {
            memoryContextService.put(createMission(), action)
        }
    }

    // Helper methods for creating test objects
    private fun createMission(): MissionKey = MissionKey("user", "agentName", "userPrompt")

    private fun createAction(): Action {
        return Action(
            object : ToolProcessor {
                override val name: String = "test"
                override val description: String = "test"
                override val args: Map<String, String> = mapOf(
                    "arg1" to "value1",
                    "arg2" to "value2"
                )
                override val keys = listOf("arg1", "arg2")

                override fun process(exchange: Exchange?) {
                    TODO("Not yet implemented")
                }
            }, mapOf(
                "arg1" to "value1",
                "arg2" to "value2"
            )
        )
    }

    // A helper method that can be used to run assertions and reduce duplicate code
    private fun <T> runAssertions(block: () -> T): T {
        var out: T? = null
        assertThat { out = block() }.isSuccess()
        return out!!
    }

    // This function will be used to create a mock TestableWeaviateClient
    private fun createTestableWeaviateClient(
        hasErrors: Boolean = false,
        memoryResults: ArrayList<Map<String, String>>
    ): TestableWeaviateClient {
        return object : TestableWeaviateClient {
            override fun data(): Data {
                return mockk<Data>().apply {
                    every { creator() } returns mockk<ObjectCreator>().apply {
                        every { withClassName(any()) } returns this
                        every { withID(any()) } returns this
                        every { withProperties(any()) } returns this
                        every { run() } returns mockk<Result<WeaviateObject>>().apply {
                            hasErrors(hasErrors)
                            every<WeaviateObject?> { result } returns mockk<WeaviateObject>()
                        }
                    }
                }
            }

            override fun graphQL(): GraphQL {
                val getResultsMap = mockk<Map<String, Any?>>()
                val memoryResultsMap = mockk<Map<String, Any?>>()
                every { getResultsMap[any()] } returns memoryResultsMap
                every { memoryResultsMap[any()] } returns memoryResults
                return mockk<GraphQL>().apply {
                    every { get() } returns mockk<Get>().apply {
                        every { withClassName(any()) } returns this
                        every { withNearText(any()) } returns this
                        every { withLimit(any()) } returns this
                        every { withFields(*anyVararg()) } returns this
                        every { run() } returns mockk<Result<GraphQLResponse>>().apply {
                            hasErrors(hasErrors)
                            every { result } returns mockk<GraphQLResponse>().apply {
                                every { data } returns getResultsMap
                            }
                        }
                    }
                }
            }

            override fun misc(): Misc = mockk()
        }
    }

    private fun Result<*>.hasErrors(hasErrors: Boolean) {
        every { hasErrors() } returns hasErrors
        if (hasErrors) {
            every { error } returns mockk<WeaviateError>().apply {
                every { messages } returns listOf(mockk<WeaviateErrorMessage>().apply {
                    every { message } returns "test"
                })
            }
        }
    }
}

