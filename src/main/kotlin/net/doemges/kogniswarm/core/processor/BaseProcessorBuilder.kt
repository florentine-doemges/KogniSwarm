package net.doemges.kogniswarm.core.processor

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.core.Message
import net.doemges.kogniswarm.core.consumer.Consumer
import net.doemges.kogniswarm.core.consumer.ConsumerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseProcessorBuilder<T : Processor> : ProcessorBuilder<T> {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected val consumers = mutableListOf<Consumer>()
    protected var input: Flow<Message<*>>? = null
    protected var output: Flow<Message<*>>? = null
    override fun <T : Consumer, X : ConsumerBuilder<T>> consumer(builder: X, block: X.() -> Unit) {
        logger.info("Adding consumer ${builder.javaClass.simpleName}")
        consumers += builder.apply(block)
            .build()
        logger.info("Consumers: ${
            consumers.joinToString(", ") { it.javaClass.simpleName }
        }"
        )

    }

    override fun input(input: Flow<Message<*>>): ConsumerBuilder<T> = apply { this.input = input }

    @Suppress("UNCHECKED_CAST")
    override fun build(): T = BaseProcessor(input) as T

}
