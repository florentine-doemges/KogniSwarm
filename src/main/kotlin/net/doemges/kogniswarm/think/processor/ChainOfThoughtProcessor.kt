package net.doemges.kogniswarm.think.processor

import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.tool.processor.ToolProcessor
import org.apache.camel.CamelContext
import org.apache.camel.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ChainOfThoughtProcessor(camelContext: CamelContext, objectMapper: ObjectMapper, private val toolProcessors: List<ToolProcessor>) :
    AbstractThinkingProcessor(camelContext, objectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    override val promptTemplate: String = """The goal is: '{goal}'
        |Given the following context:
        |{context}
        |And your action history:
        |{actionHistory}
        |Please choose a tool from the following list:
        |{tools}
        |What should be the next action? 
        |Please respond in the format '<action_name> <parameters>'.""".trimMargin()

    override fun processResponse(message: Message, responseText: String?) {
        val strippedAndTrimmed = responseText?.removeSurrounding("'")?.trim()
        logger.info("responseText: $strippedAndTrimmed")
        val actionName = strippedAndTrimmed?.substringBefore(" ")
        val actionParameters = strippedAndTrimmed?.substringAfter(" ")
        logger.debug("actionName: $actionName")
        logger.debug("actionParameters: $actionParameters")

        val name = toolProcessors.find {
            actionName?.contains(it.name, ignoreCase = true) ?: false
        }?.name ?: error("Could not find any tool matching '$actionName'")

        logger.debug("name: $name")

        message.headers["toolUri"] = "tool:$name"
        message.headers["toolParams"] = actionParameters

        logger.info("ToolUri: ${message.headers["toolUri"]}")
        logger.info("ToolParams: ${message.headers["toolParams"]}")
    }


}
