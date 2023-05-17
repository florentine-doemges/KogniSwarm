package net.doemges.kogniswarm.core.consumer

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.core.ComponentBuilder
import net.doemges.kogniswarm.core.Message

interface ConsumerBuilder<X: Consumer> : ComponentBuilder<X> {
    fun input(input: Flow<Message<*>>): ConsumerBuilder<X>
}