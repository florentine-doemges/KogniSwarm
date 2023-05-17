package net.doemges.kogniswarm.assistant

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.core.consumer.BaseConsumer
import net.doemges.kogniswarm.core.consumer.BaseConsumerBuilder
import net.doemges.kogniswarm.core.Message

class AssistantConsumer(
    input: Flow<Message<*>>? = null
) : BaseConsumer(
    input
) {
    companion object {
        fun builder(block: Builder.() -> Unit = {}): Builder =
            Builder().apply(block)


    }

    class Builder : BaseConsumerBuilder<AssistantConsumer>() {
        override fun build(): AssistantConsumer =
            AssistantConsumer(input)

    }

}