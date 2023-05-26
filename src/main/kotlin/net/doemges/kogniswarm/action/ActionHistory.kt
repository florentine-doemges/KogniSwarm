package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.core.Mission
import org.springframework.stereotype.Component

@Component
class ActionHistory {
    private val history: MutableMap<Mission, MutableList<Action>> = mutableMapOf()

    fun put(mission: Mission, action: Action) {
        history.getOrPut(mission) { mutableListOf() }
            .add(action)
    }

    fun remove(mission: Mission) {
        history.remove(mission)
    }

    fun get(mission: Mission): List<Action>? = history.getOrPut(mission) { mutableListOf() }


}