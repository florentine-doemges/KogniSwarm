package net.doemges.kogniswarm.core

import net.doemges.kogniswarm.core.consumer.Consumer
import net.doemges.kogniswarm.core.consumer.ConsumerBuilder
import net.doemges.kogniswarm.core.processor.Processor
import net.doemges.kogniswarm.core.processor.ProcessorBuilder
import net.doemges.kogniswarm.core.producer.Producer
import net.doemges.kogniswarm.core.producer.ProducerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KogniSwarm(val components: List<Component>) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun start() {
        logger.info("Starting KogniSwarm")
        components.forEach {
            logger.info("Starting component ${it.javaClass.simpleName}")
            it.start()
        }
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        fun builder(block: Builder.() -> Unit): Builder {
            logger.info("Building KogniSwarm")
            return Builder().apply(block)
                .also {
                    logger.info("Built KogniSwarm")
                }
        }
    }

    class Builder {

        private val logger: Logger = LoggerFactory.getLogger(this::class.java)

        private val components: MutableList<Component> = mutableListOf()

        fun <T : Producer, X : ProducerBuilder<T>> producer(
            builder: X,
            block: X.() -> Unit = {}
        ) {
            components += builder
                .apply(block)
                .build()
        }


        fun <T : Processor, X : ProcessorBuilder<T>> processor(
            builder: X,
            block: X.() -> Unit = {}
        ) {
            components += builder
                .apply(block)
                .build()
        }

        fun <T : Consumer, X : ConsumerBuilder<T>> consumer(
            builder: X,
            block: X.() -> Unit = {}
        ) {
            components += builder
                .apply(block)
                .build()
        }

        fun build(): KogniSwarm {
            logger.info("Building KogniSwarm")
            return KogniSwarm(components).apply {
                components.windowed(2) { (first, second) ->
                    connect(first, second)
                }
            }
                .also { logger.info("Built KogniSwarm") }
        }

        private fun connect(first: Component, second: Component) {
            val firstName = first.javaClass.simpleName
            val secondName = second.javaClass.simpleName
            logger.info("Connecting $firstName to $secondName")
            if (first is Producer && second is Consumer) {
                second.input = first.output()
            } else {
                error("Cannot connect $firstName to $secondName")
            }
            logger.info("Connected  $firstName to $secondName")
        }
    }

}