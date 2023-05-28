package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.core.Mission
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ActionHistory {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val history: MutableMap<Mission, MutableList<Action>> = mutableMapOf()

    fun put(mission: Mission, action: Action) {
        logger.info("Adding to action history: $action")
        history.getOrPut(mission) { mutableListOf() }
            .add(action)
    }

    fun get(mission: Mission): List<Action>? = history
        .getOrPut(mission) { mutableListOf() }
        .also {
            logger.info("History Size: ${it.size}")
        }

    fun clear() {
        history.clear()
    }
}