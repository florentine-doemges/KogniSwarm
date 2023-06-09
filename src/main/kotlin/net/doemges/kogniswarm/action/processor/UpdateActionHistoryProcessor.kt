package net.doemges.kogniswarm.action.processor

import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.action.service.ActionHistoryService
import net.doemges.kogniswarm.mission.model.MissionKey
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateActionHistoryProcessor(private val actionHistoryService: ActionHistoryService) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun process(exchange: Exchange) {
        val missionKey = exchange.getIn().body as MissionKey
        exchange.getIn().headers["action"]?.also {
            logger.debug("Adding to action history: $it")
            actionHistoryService.put(missionKey, it as Action)
        } ?: logger.debug("No action to add to action history.")
    }

}