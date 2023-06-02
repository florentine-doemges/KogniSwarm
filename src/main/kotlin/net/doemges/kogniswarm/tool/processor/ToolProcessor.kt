package net.doemges.kogniswarm.tool.processor

import org.apache.camel.Processor

interface ToolProcessor : Processor {
    val name: String
    val description: String
    val args: Map<String, String>
    val keys: List<String>
}