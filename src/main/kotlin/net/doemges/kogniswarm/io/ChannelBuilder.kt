package net.doemges.kogniswarm.io

import kotlinx.coroutines.channels.Channel

class ChannelBuilder<X : Message<*>> {
    fun build(): Channel<X> = Channel()
}

fun <X : Message<*>> createChannel(block: ChannelBuilder<X>.() -> Unit = {}): Channel<X> = ChannelBuilder<X>().run {
    block()
    build()
}