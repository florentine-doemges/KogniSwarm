package net.doemges.kogniswarm.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ParameterParser(private val objectMapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun parseParameters(toolParams: String, keys: List<String>): Map<String, String> = toolParams
        .removeSurrounding("[", "]")
        .replace('\'', '\"')
        .let { standardizedToolParams ->
            runCatching {
                objectMapper.readValue(standardizedToolParams, object : TypeReference<Map<String, String>>() {})
            }.getOrElse { parseAsKeyValuePairs(standardizedToolParams, keys) }
        }

    private fun parseAsKeyValuePairs(toolParams: String, keys: List<String>): Map<String, String> {
        logger.debug("Parsing $toolParams")
        val input = toolParams
            .replace("args:", "")
            .trim()
            .removeSurrounding("[", "]")
        logger.debug("Parsing $input")
        val toList = Regex("""\s*([a-zA-Z:\s]*"[^"]*"|[^,\s]*)\s*""")
            .findAll(input)
            .toList()
        logger.debug("Parsing $toList")
        val map = toList
            .map { it.value }
        logger.debug("Parsing $map")
        val filter = map
            .filter { it.isNotBlank() }
        logger.debug("Parsing $filter")
        val map1 = filter
            .map {
                it.trim()
                    .removeSurrounding("\"")
            }
        logger.debug("Parsing $map1")
        val map2 = map1
            .map { part ->
                part.split("=", ":", limit = 2)
                    .filter { it.isNotBlank() }
            }
        logger.debug("Parsing $map2")
        val mapIndexed = map2
            .mapIndexed { index, parts ->
                if (parts.size == 1) {
                    keys[index] to parts.first()
                } else {
                    parts.first() to parts.last().trim()
                        .removeSurrounding("\"")
                }
            }
        logger.debug("Parsing $mapIndexed")
        val toMap = mapIndexed
            .toMap()
        logger.debug("Parsing $toMap")
        return toMap
    }

}
