package net.doemges.kogniswarm.core

import org.apache.camel.CamelContext

fun <T> CamelContext.sendMessage(uri: String, body: T) = this.createProducerTemplate()
    .sendBody(uri, body)

@Suppress("UNCHECKED_CAST")
fun <I, O> CamelContext.sendRequest(uri: String, body: I, type: Class<O>): O = this.createProducerTemplate()
    .requestBody(uri, body, type) as O