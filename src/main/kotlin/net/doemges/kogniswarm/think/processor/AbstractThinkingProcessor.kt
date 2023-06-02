package net.doemges.kogniswarm.think.processor

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.core.model.Mission
import net.doemges.kogniswarm.openai.model.OpenAIChatCompletionRequest
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor

abstract class AbstractThinkingProcessor(
    private val camelContext: CamelContext,
    private val objectMapper: ObjectMapper
) : Processor {
    protected abstract val promptTemplate: String

    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        val message: Message = exchange.getIn()
        val mission = message.body as Mission
        val openAIChatCompletionRequest = OpenAIChatCompletionRequest
            .builder {
                optimize(true)
                modelRequest {
                    modelName("gpt")
                    isGPT4allowed(false)
                }
                messages {
                    prompt {
                        template {
                            message {
                                content(promptTemplate)
                                role(ChatRole.User)
                            }
                            message {
                                content(
                                    """
                                    Answer exactly in the given format without any change or comment because it will be parsed by a machine.
                                    """
                                )
                                role(ChatRole.System)
                            }
                        }
                        variable(
                            "actionHistory",
                            message.headers["actionHistory"]?.let { objectMapper.writeValueAsString(it) }
                                ?: "no action history yet"
                        )
                        variable("context", message.headers["context"] as? String ?: "no context yet")
                        variable("tools", message.headers["toolDescriptions"] as? String ?: "no tools")
                        variable("goal", mission.userPrompt)
                        variable("action", message.headers["action"]?.let { objectMapper.writeValueAsString(it) } ?: "")
                    }
                }
            }
            .build()

        val requestBody = camelContext
            .createProducerTemplate()
            .requestBody("direct:openai-chatcompletion", openAIChatCompletionRequest)

        val responseText = try {
            (requestBody as ChatCompletion).choices.first().message?.content
        } catch (e: NoSuchElementException) {
            "No answer from OpenAI"
        }
        processResponse(message, responseText)
    }

    protected abstract fun processResponse(message: Message, responseText: String?)
}