package net.doemges.kogniswarm.context

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.Mission
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MemoryContext(private val weaviateClient: WeaviateClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun put(mission: Mission, action: Action) {

        val run = weaviateClient.data()
            .creator()
            .withClassName("Memory")
            .withID(
                UUID.randomUUID()
                    .toString()
            )
            .withProperties(
                mapOf(
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
                )
            )
            .run()
        if (run.hasErrors()) {
            error(run.error.messages.joinToString("\n"))
        }
    }

    fun get(mission: Mission): String {
        val withNearText = weaviateClient
            .graphQL()
            .get()
            .withClassName("Memory")
            .withNearText(
                NearTextArgument.builder()
                    .concepts(arrayOf(mission.userPrompt))
                    .build()
            )
            .withLimit(5)
            .withFields(
                Field.builder()
                    .name("result")
                    .build(),
                Field.builder()
                    .name("userName")
                    .build(),
                Field.builder()
                    .name("agentName")
                    .build(),
                Field.builder()
                    .name("userPrompt")
                    .build(),
                Field.builder()
                    .name("toolName")
                    .build(),
                Field.builder()
                    .name("toolDescription")
                    .build(),
                Field.builder()
                    .name("args")
                    .build()
            )
        val graphQLResponseResult = withNearText
            .run()
        val graphQLResponse = graphQLResponseResult
            .result

        val data: Map<String, Any?> = graphQLResponse.data as Map<String, Any?>
        val get: Map<String, Any?> = data["Get"] as Map<String, Any?>
        val memory: ArrayList<Map<String, String>> = get["Memory"] as ArrayList<Map<String, String>>

        val out = memory.joinToString("\n") {
            "- '${it["agentName"]}' utilized the '${it["toolName"]}' function that ${it["toolDescription"]} based on the following parameters: " +
                "${it["args"]} to get the following result: ${it["result"]}. This action was initiated in response to ${it["userName"]}'s request: " +
                "'${it["userPrompt"]}'"
        }
        return out


    }

}

data class Query(@Id val id: UUID, val userPrompt: String)

data class Memento(
    @Id val id: UUID,
    val userName: String,
    val agentName: String,
    val userPrompt: String,
    val toolDescription: String,
    val toolName: String,
    val args: Map<String, String>,
    val result: String
)
