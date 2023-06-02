package net.doemges.kogniswarm.action.service

import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.core.model.Mission
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ActionHistoryService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val history: MutableMap<Mission, MutableList<Action>> = mutableMapOf()

    fun put(mission: Mission, action: Action) {
        logger.debug("Adding to action history: $action")
        history.getOrPut(mission) { mutableListOf() }
            .add(action)
    }

    fun get(mission: Mission): List<Action>? = history
        .getOrPut(mission) { mutableListOf() }
        .sortedBy { it.timestamp }
        .also {
            logger.debug("History Size: ${it.size}")
        }

    fun clear() {
        history.clear()
    }
}