package net.doemges.kogniswarm.structure

import kotlinx.coroutines.channels.Channel
import net.doemges.kogniswarm.io.InputGateway
import net.doemges.kogniswarm.io.InputGatewayBuilder
import net.doemges.kogniswarm.io.Message
import net.doemges.kogniswarm.io.MessageProcessor
import net.doemges.kogniswarm.io.OutputGateway
import net.doemges.kogniswarm.io.OutputGatewayBuilder
import net.doemges.kogniswarm.io.createChannel

class Architecture(
    id: String,
    @Suppress("unused") val inputGateways: List<InputGateway<*, *>>,
    @Suppress("unused") val outputGateways: List<OutputGateway<*, *>>,
    @Suppress("unused") val messageProcessors: List<MessageProcessor>,
    @Suppress("unused") val channels: List<Channel<Message<*>>>
) : Component<Architecture>(id)

class ArchitectureBuilder(id: String) : ComponentBuilder<Architecture>(id) {

    private val inputGateways = mutableListOf<InputGateway<*, *>>()
    private val outputGateways = mutableListOf<OutputGateway<*, *>>()
    private val messageProcessors = mutableListOf<MessageProcessor>()
    private val channels = mutableListOf<Channel<Message<*>>>()

    fun <T, R> inputGateway(id: String, block: InputGatewayBuilder<T, R>.() -> Unit = {}) =
        (createComponent(InputGatewayBuilder(id), block) as InputGateway).also { inputGateways.add(it) }

    fun <T, R> outputGateway(id: String, block: OutputGatewayBuilder<T, R>.() -> Unit = {}) =
        (createComponent(OutputGatewayBuilder(id), block) as OutputGateway).also { outputGateways.add(it) }

    fun <X : MessageProcessor> messageProcessor(instance: X) = instance.also { messageProcessors.add(it) }

    @Suppress("UNCHECKED_CAST")
    fun <X : Message<*>> channel() = createChannel<X>().also { channels.add(it as Channel<Message<*>>) }

    override fun build(): Architecture = Architecture(id, inputGateways, outputGateways, messageProcessors, channels)


}