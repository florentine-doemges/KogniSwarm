package net.doemges.kogniswarm.action.model

import net.doemges.kogniswarm.tool.processor.ToolProcessor

data class Action(
    val toolProcessor: ToolProcessor,
    val args: Map<String, String>,
    var result: String? = null,
    var description: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)