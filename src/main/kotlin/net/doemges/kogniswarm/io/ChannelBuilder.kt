package net.doemges.kogniswarm.io

import kotlinx.coroutines.channels.Channel
import net.doemges.kogniswarm.io.model.Message

class ChannelBuilder<X : Message<*>> {
    fun build(): Channel<X> = Channel()
}

fun <X : Message<*>> createChannel(@Suppress("unused") block: ChannelBuilder<X>.() -> Unit = {}): Channel<X> = ChannelBuilder<X>().run {
    block()
    build()
}