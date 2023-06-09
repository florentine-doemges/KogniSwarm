package net.doemges.kogniswarm.action.service

import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.mission.model.MissionKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ActionHistoryService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val history: MutableMap<MissionKey, MutableList<Action>> = mutableMapOf()

    fun put(missionKey: MissionKey, action: Action) {
        logger.debug("Adding to action history: $action")
        history.getOrPut(missionKey) { mutableListOf() }
            .add(action)
    }

    fun get(missionKey: MissionKey): List<Action>? = history
        .getOrPut(missionKey) { mutableListOf() }
        .sortedBy { it.timestamp }
        .also {
            logger.debug("History Size: ${it.size}")
        }

    fun clear() {
        history.clear()
    }
}