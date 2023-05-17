package net.doemges.kogniswarm.core.processor

import net.doemges.kogniswarm.core.producer.ProducerBuilder
import net.doemges.kogniswarm.core.consumer.Consumer
import net.doemges.kogniswarm.core.consumer.ConsumerBuilder

interface ProcessorBuilder<X : Processor> : ConsumerBuilder<X>, ProducerBuilder<X> {
    fun <T : Consumer, X : ConsumerBuilder<T>> consumer(
        builder: X,
        block: X.() -> Unit = {}
    )
}