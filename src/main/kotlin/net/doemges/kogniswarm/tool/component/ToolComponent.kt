package net.doemges.kogniswarm.tool.component

import net.doemges.kogniswarm.tool.processor.ToolProcessor
import org.apache.camel.Consumer
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.Producer
import org.apache.camel.support.DefaultComponent
import org.apache.camel.support.DefaultEndpoint
import org.apache.camel.support.DefaultProducer
import org.springframework.stereotype.Component

@Component("tool")
class ToolComponent(val toolProcessors: List<ToolProcessor>) : DefaultComponent() {
    override fun createEndpoint(uri: String, remaining: String, parameters: MutableMap<String, Any>): Endpoint =
        ToolEndpoint(
            uri,
            this,
            toolProcessors.find { it.name == remaining } ?: error("Tool $remaining not found!")
        )
}

class ToolEndpoint(uri: String, component: ToolComponent, private val toolProcessor: ToolProcessor) :
    DefaultEndpoint(uri, component) {

    override fun createProducer(): Producer = ToolProducer(this, toolProcessor)

    override fun createConsumer(processor: Processor): Consumer =
        throw UnsupportedOperationException("Cannot consume from ToolEndpoint")
}

class ToolProducer(endpoint: Endpoint, private val toolProcessor: ToolProcessor) : DefaultProducer(endpoint) {
    override fun process(exchange: Exchange) {
        toolProcessor.process(exchange)
    }

}
