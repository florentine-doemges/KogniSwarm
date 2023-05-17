package net.doemges.kogniswarm.core.consumer

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.core.Message

open class BaseConsumerBuilder<X : Consumer> : ConsumerBuilder<X> {

    protected var input: Flow<Message<*>>? = null
    @Suppress("UNCHECKED_CAST")
    override fun build(): X = BaseConsumer(input) as X

    override fun input(input: Flow<Message<*>>): ConsumerBuilder<X> = apply { this.input = input }
}