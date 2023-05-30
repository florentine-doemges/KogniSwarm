package net.doemges.kogniswarm.action

import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.core.Mission
import net.doemges.kogniswarm.token.TokenizerService
import net.doemges.kogniswarm.token.limitTokens
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ActionHistoryProcessor(
    private val actionHistory: ActionHistory,
    private val objectMapper: ObjectMapper,
    val tokenizerService: TokenizerService
) : Processor {


    private val logger = LoggerFactory.getLogger(javaClass)
    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        val actionList = actionHistory.get(mission)
        val history = actionList
            ?.reversed()
            ?.limitTokens(1000, tokenizerService.tokenizer) {
                objectMapper.writeValueAsString(it)
            }
            ?.reversed()
            ?: emptyList()
        logger.info("History Size: ${history.size}")
        exchange.getIn().headers["actionHistory"] = history
    }

}