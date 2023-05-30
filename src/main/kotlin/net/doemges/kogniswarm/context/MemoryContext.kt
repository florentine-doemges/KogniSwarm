package net.doemges.kogniswarm.context

import io.weaviate.client.v1.graphql.query.Get
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.Mission
import net.doemges.kogniswarm.weaviate.TestableWeaviateClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MemoryContext(@Lazy private val weaviateClient: TestableWeaviateClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun put(mission: Mission, action: Action, id: UUID = UUID.randomUUID()) {
        val run = weaviateClient.data()
            .creator()
            .withClassName("Memory")
            .withID(id.toString())
            .withProperties(createPropertiesMap(mission, action))
            .run()
        if (run.hasErrors()) {
            val errorMessage = run.error.messages.joinToString("\n") { it.message }
            logger.error(errorMessage)
            error(errorMessage)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(mission: Mission, limit: Int = 10): String {
        val withNearText = createNearTextQuery(mission, limit)

        val graphQLResponseResult = withNearText?.run()
        val graphQLResponse = graphQLResponseResult?.result

        val data: Map<String, Any?> = graphQLResponse?.data as Map<String, Any?>
        val get: Map<String, Any?> = data["Get"] as Map<String, Any?>
        val memory: ArrayList<Map<String, String>> = get["Memory"] as ArrayList<Map<String, String>>

        return formatMemory(memory)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createPropertiesMap(mission: Mission, action: Action): Map<String, Any> {
        val map = mapOf(
            "userName" to mission.user,
            "agentName" to mission.agentName,
            "userPrompt" to mission.userPrompt,
            "toolName" to action.tool.name,
            "toolDescription" to action.tool.description,
            "args" to action.args.keys.joinToString(", ") {
                "$it=${action.args[it]}"
            },
            "result" to action.result,
            "description" to action.description
        ) as Map<String, Any>

        logger.info("Creating: $map")

        return map
    }

    private fun createNearTextQuery(mission: Mission, limit: Int = 10): Get? {
        return weaviateClient
            .graphQL()
            .get()
            .withClassName("Memory")
            .withNearText(
                NearTextArgument.builder()
                    .concepts(arrayOf(mission.userPrompt))
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
                createField("args")
            )
    }

    private fun createField(fieldName: String): Field? = Field.builder()
        .name(fieldName)
        .build()

    private fun formatMemory(memory: ArrayList<Map<String, String>>): String = memory.joinToString("\n") {
        "- You utilized the tool '${it["toolName"]}' and got the result '${it["description"]}'"
    }

}
