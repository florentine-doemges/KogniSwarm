package net.doemges.kogniswarm.context.service

import io.weaviate.client.v1.graphql.query.Get
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.WeaviateClass
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.core.functions.limitTokens
import net.doemges.kogniswarm.mission.model.MissionKey
import net.doemges.kogniswarm.token.service.TokenizerService
import net.doemges.kogniswarm.weaviate.util.TestableWeaviateClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@DependsOn("memoryClass")
@Profile("!test")
class MemoryContextService(
    @Lazy private val weaviateClient: TestableWeaviateClient,
    private val tokenizerService: TokenizerService,
    @Suppress("UNUSED_PARAMETER") memoryClass: WeaviateClass
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun put(missionKey: MissionKey, action: Action, id: UUID = UUID.randomUUID()) {
        val run = weaviateClient.data()
            .creator()
            .withClassName("Memory")
            .withID(id.toString())
            .withProperties(createPropertiesMap(missionKey, action))
            .run()
        if (run.hasErrors()) {
            val errorMessage = run.error.messages.joinToString("\n") { it.message }
            logger.error(errorMessage)
            error(errorMessage)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(missionKey: MissionKey, limit: Int = 10, maxToken: Int = 4000): ArrayList<Map<String, String>> {
        val withNearText = createNearTextQuery(missionKey, limit)

        val graphQLResponseResult = withNearText?.run()
        val graphQLResponse = graphQLResponseResult?.result

        val data: Map<String, Any?> = graphQLResponse?.data as Map<String, Any?>
        val get: Map<String, Any?> = data["Get"] as Map<String, Any?>
        return get["Memory"] as ArrayList<Map<String, String>>
    }

    fun formatContext(
        memoryMap: ArrayList<Map<String, String>>,
        maxToken: Int
    ): String {
        if (memoryMap.isEmpty()) {
            return "no context yet"
        }
        val memory: List<Map<String, String>> = memoryMap
            .sortedBy {
                val timestampString = it["timestamp"]
                val toLong = timestampString?.toLong()
                    ?.unaryMinus() ?: 0L
                toLong
            }
            .limitTokens(maxToken, tokenizerService.tokenizer) {
                formatMemories(it)
            }
            .reversed()

        return formatMemories(memory)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createPropertiesMap(missionKey: MissionKey, action: Action): Map<String, Any> {
        val map = mapOf(
            "userName" to missionKey.user,
            "agentName" to missionKey.agentName,
            "userPrompt" to missionKey.userPrompt,
            "toolName" to action.toolProcessor.name,
            "toolDescription" to action.toolProcessor.description,
            "args" to action.args.keys.joinToString(", ") {
                "$it=${action.args[it]}"
            },
            "result" to action.result,
            "description" to action.description,
            "timestamp" to action.timestamp
        ) as Map<String, Any>

        logger.debug("Creating: $map")

        return map
    }

    private fun createNearTextQuery(missionKey: MissionKey, limit: Int = 10): Get? {
        return weaviateClient
            .graphQL()
            .get()
            .withClassName("Memory")
            .withNearText(
                NearTextArgument.builder()
                    .concepts(arrayOf(missionKey.userPrompt))
                    .build()
            )
            .withLimit(limit)
            .withFields(
                createField("description"),
                createField("result"),
                createField("userName"),
                createField("agentName"),
                createField("userPrompt"),
                createField("toolName"),
                createField("toolDescription"),
                createField("args"),
                createField("timestamp")
            )
    }

    private fun createField(fieldName: String): Field? = Field.builder()
        .name(fieldName)
        .build()

    private fun formatMemories(memories: List<Map<String, String>>): String = memories
        .joinToString("\n") { formatMemory(it) }

    private fun formatMemory(memory: Map<String, String>) =
        "- You utilized the tool '${memory["toolName"]}' and got the result '${memory["description"]}'"

}
