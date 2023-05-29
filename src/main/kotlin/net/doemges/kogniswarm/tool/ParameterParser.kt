package net.doemges.kogniswarm.tool

import org.springframework.stereotype.Component

@Component
class ParameterParser {
    fun parseParameters(toolParams: String) = toolParams.split(", ")
        .associate {
            it.split(":")
                .let { (key, value) ->
                    key to value.trim()
                        .removeSurrounding("\"")
                }
        }
}