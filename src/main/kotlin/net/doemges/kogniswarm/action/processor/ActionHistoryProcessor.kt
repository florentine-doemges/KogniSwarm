package net.doemges.kogniswarm.action.processor

import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.action.service.ActionHistoryService
import net.doemges.kogniswarm.core.functions.limitTokens
import net.doemges.kogniswarm.core.model.Mission
import net.doemges.kogniswarm.token.service.TokenizerService
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ActionHistoryProcessor(
    private val actionHistoryService: ActionHistoryService,
    private val objectMapper: ObjectMapper,
    val tokenizerService: TokenizerService
) : Processor {


    private val logger = LoggerFactory.getLogger(javaClass)
    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        val actionList = actionHistoryService.get(mission)
        val history = actionList
            ?.reversed()
            ?.limitTokens(1000, tokenizerService.tokenizer) {
                objectMapper.writeValueAsString(it)
            }
            ?.reversed()
            ?: emptyList()
        logger.debug("History Size: ${history.size}")
        exchange.getIn().headers["actionHistory"] = history
    }

}