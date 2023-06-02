package net.doemges.kogniswarm.openai.processor

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.openai.model.OpenAIChatCompletionRequest
import net.doemges.kogniswarm.openai.model.OpenAIChatMessage
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpenAIChatCompletionOptimizer(private val openAI: OpenAI) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        val openAIChatCompletionRequest = exchange.getIn().body as OpenAIChatCompletionRequest

        if (!openAIChatCompletionRequest.optimize) return

        logger.info("Optimizing Chat Completion Request: ${openAIChatCompletionRequest.messages}")

        val messages = openAIChatCompletionRequest.messages?.filter { it.role == ChatRole.User }
            ?.joinToString("\n") {
                "${it.role?.role ?: "unknown"}: \"${it.content}\""
            } ?: ""

        val prompt =
            """The following is a prompt to ChatGPT. It was generated by an automated process and might have some flaws.
                |The tool descriptions are very important and should not be shortened.
                |Optimize the prompt to make it more clear and concise without losing any information and without blurring the original intent:
                |
                |$messages
                |""".trimMargin()

        val completionRequest = OpenAIChatCompletionRequest.builder {
            openAIChatCompletionRequest.model?.also { model(it) }
            openAIChatCompletionRequest.modelRequest?.also { modelRequest(it) }
            maxTokens(3000)
            messages {
                message(OpenAIChatMessage.builder {
                    role(ChatRole.User)
                    content(prompt)
                }
                    .build())
                message(OpenAIChatMessage.builder {
                    role(ChatRole.System)
                    content(
                        """Answer without any comment or additon, only the prompt, because it will be parsed by a machine."""
                    )
                }
                    .build())
            }
            temperature(0.1)
        }
            .build()


        val chatCompletion = runBlocking { openAI.chatCompletion(completionRequest.asChatCompletionRequest(openAI)) }

        val content = chatCompletion.choices[0].message?.content

        logger.info(content)

        if (content != null) {
            exchange.message.body = openAIChatCompletionRequest.modifyUserMessageContent(content)
        }

    }
}