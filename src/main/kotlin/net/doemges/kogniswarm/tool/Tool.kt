package net.doemges.kogniswarm.tool

import org.apache.camel.Processor

interface Tool : Processor {
    val name: String
    val description: String
    val args: Map<String, String>
    val keys: List<String>
}