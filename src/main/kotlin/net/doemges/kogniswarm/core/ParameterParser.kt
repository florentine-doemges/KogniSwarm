package net.doemges.kogniswarm.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ParameterParser {

    private val objectMapper = ObjectMapper()

    fun parseParameters(toolParams: String, keys: List<String>): Map<String, String> = toolParams
        .removeSurrounding("[", "]")
        .replace('\'', '\"')
        .let { standardizedToolParams ->
            runCatching {
                objectMapper.readValue(standardizedToolParams, object : TypeReference<Map<String, String>>() {})
            }.getOrElse { parseAsKeyValuePairs(standardizedToolParams, keys) }
        }

    private fun parseAsKeyValuePairs(toolParams: String, keys: List<String>): Map<String, String> {
        val findAll = Regex("""\s*(\"[^\"]*\"|[^\s]*)\s*""")
            .findAll(toolParams)
            .toList()
            .map { it.value }
            .filter { it.isNotBlank() }

        val split = findAll
            .map {
                it.trim()
                    .removeSurrounding("\"")
            }
            .map { part ->
                part.split("=", ":", limit = 2)
                    .filter { it.isNotBlank() }
            }
        val mapIndexed = split
            .mapIndexed { index, parts ->
                if (parts.size == 1) {
                    keys[index] to parts.first()
                } else {
                    parts.first() to parts.last()
                }
            }
        return mapIndexed
            .toMap()
    }

}
