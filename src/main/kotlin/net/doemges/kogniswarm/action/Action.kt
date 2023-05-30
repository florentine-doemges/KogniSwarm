package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.tool.Tool

data class Action(
    val tool: Tool,
    val args: Map<String, String>,
    var result: String? = null,
    var description: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)