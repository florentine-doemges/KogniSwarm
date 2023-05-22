package net.doemges.kogniswarm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.image.internal.ImageResponseFormat.Companion.url
import net.doemges.kogniswarm.agent.AgentRequest
import net.doemges.kogniswarm.command.GoogleSearchCommand
import net.doemges.kogniswarm.core.sendRequest
import net.doemges.kogniswarm.openai.OpenAIChatCompletionRequest
import org.apache.camel.CamelContext
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class KogniSwarmRunner(
    private val camelContext: CamelContext,
    private val googleSearchCommand: GoogleSearchCommand
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @OptIn(BetaOpenAI::class)
    override fun run(args: ApplicationArguments?) {
        logger.info("Starting KogniSwarm")
//        camelContext.sendMessage("direct:discord", "Hello World")
//        val openAIModelRequest = OpenAIModelRequest
//            .builder {
//                modelName("gpt")
//                isGPT4allowed(false)
//            }
//            .build()


//        println(camelContext.sendRequest("direct:openai-models", openAIModelRequest, List::class.java)
//            .map { it as Model }
//            .joinToString()
//        )

        val builder = OpenAIChatCompletionRequest
            .builder {
                chatCompletionConfig()
            }
        val openAIChatCompletionRequest = builder.build()

        println(
            camelContext.sendRequest(
                "direct:openai-chatcompletion",
                openAIChatCompletionRequest,
                ChatCompletion::class.java
            )
        )

        val agentRequest: AgentRequest = AgentRequest
            .builder {
                request {
                    chatCompletionConfig()
                }
                history {
                    action<GoogleSearchCommand.Builder>{
                        arg("query", "Bushcraft")
                        output<GoogleSearchCommand.OutputBuilder> {
                            results {
                                result {
                                    url("https://survival-kompass.de/was-ist-bushcraft/")
                                    title("Was ist Bushcraft? Überlebensleitfaden für die Wildnis")
                                }
                                result {
                                    url("https://bushcraftcamp.de/")
                                    title("Bushcraftcamp - Outdoor Bushcraft Survival")
                                }
                            }
                        }
                    }

                }
            }
            .build()
    }

    @OptIn(BetaOpenAI::class)
    private fun OpenAIChatCompletionRequest.Builder.chatCompletionConfig() {
        modelRequest {
            modelName("gpt")
            isGPT4allowed(false)
        }
        messages {
            prompt {
                template {
                    message {
                        content("Hello, tell me a joke about {topic}.")
                        role(ChatRole.User)
                    }
                    message {
                        content("Tell it from the perspective of a {species}")
                        role(ChatRole.System)
                    }
                }
                variable("topic", "Bushcraft")
                variable("species", "dog")
            }
        }
    }


}


