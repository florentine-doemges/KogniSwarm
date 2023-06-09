package net.doemges.kogniswarm.action.processor

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.mission.model.MissionKey
import net.doemges.kogniswarm.core.functions.formatTimestamp
import net.doemges.kogniswarm.openai.model.OpenAIChatCompletionRequest
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ActionSummaryProcessor(
    private val objectMapper: ObjectMapper,
    private val camelContext: CamelContext
) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        val message = exchange.getIn()
        (message.headers["action"] as? Action)?.let { action ->
            logger.debug(action.toString())
            val openAIChatCompletionRequest = OpenAIChatCompletionRequest
                .builder {
                    modelRequest {
                        modelName("gpt")
                        isGPT4allowed(false)
                    }
                    messages {
                        prompt {
                            template {
                                message {
                                    content("""Describe this action object in a short, structured and consistent manner: {action}""".trimIndent())
                                    role(ChatRole.User)
                                }
                                message {
                                    content(
                                        "This object represents an action performed by the tool ${action.toolProcessor.name}. " +
                                            "The tool ${action.toolProcessor.description}. " +
                                            "The action is carried out following the goal '{goal}'. " +
                                            "Do not in any case mention the tool or the again, because it is known to the audience and time is short. " +
                                            "Use the time to focus on the results instead."
                                    )
                                    role(ChatRole.System)
                                }
                            }
                            variable(
                                "action",
                                objectMapper.writeValueAsString(action)
                            )
                            variable(
                                "goal",
                                (message.body as MissionKey).userPrompt
                            )
                        }
                    }
                }
                .build()

            val requestBody = camelContext
                .createProducerTemplate()
                .requestBody("direct:openai-chatcompletion", openAIChatCompletionRequest)

            val responseText = (requestBody as ChatCompletion).choices.first().message?.content?.replace(
                action.timestamp.toString(),
                formatTimestamp(action.timestamp, "yyyy-MM-dd HH:mm:ss")
            )

            logger.info("SUMMARY: $responseText")

            action.description = responseText
        }
    }

}
