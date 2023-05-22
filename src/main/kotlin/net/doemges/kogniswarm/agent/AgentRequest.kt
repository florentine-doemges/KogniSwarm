package net.doemges.kogniswarm.agent

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequestBuilder
import net.doemges.kogniswarm.command.Command
import net.doemges.kogniswarm.command.CommandBuilder
import net.doemges.kogniswarm.openai.OpenAIChatCompletionRequest

@OptIn(BetaOpenAI::class)
class AgentRequest(
    val chatCompletionRequestBuilder: ChatCompletionRequestBuilder,
    val agentHistory: AgentHistory
) {
    companion object {
        fun builder(function: Builder.() -> Unit): Builder {
            TODO("Not yet implemented")
        }
    }

    class Builder {
        private var chatCompletionRequestBuilder: (OpenAIChatCompletionRequest.Builder.() -> Unit)? = null
        fun request(block: OpenAIChatCompletionRequest.Builder.() -> Unit) = apply {
            chatCompletionRequestBuilder = block
        }

        fun history(function: AgentHistory.Builder.() -> Unit) {
            TODO("Not yet implemented")
        }

        fun build(): AgentRequest {
            TODO("Not yet implemented")
        }


    }
}

class AgentAction<T : Command> {
    companion object {
        fun <T : Command> builder(function: Builder<T>.() -> Unit): Any {
            TODO()
        }
    }

    class Builder<T : Command> {
        private val args = mutableMapOf<String, String>()
        fun arg(key: String, value: String) {
            args[key] = value
        }

        fun output(function: () -> Unit) {
            TODO("Not yet implemented")
        }

    }
}

class AgentHistory {
    companion object {
        fun builder(function: Builder.() -> Unit): Any {
            TODO()
        }
    }

    class Builder {
        inline fun <reified T: CommandBuilder> action(noinline init: T.() -> Unit): T {
            val command = T::class.java.newInstance()
            command.init()
            return command
        }

    }
}
