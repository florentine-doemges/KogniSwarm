package net.doemges.kogniswarm.mission.model

import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.agent.service.Agent
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document
class Mission(
    @Id var id: UUID = UUID.randomUUID(),
    var missionKey: MissionKey,
    var agent: Agent,
    actions: MutableList<Action> = mutableListOf()
) {
    val actions: MutableList<Action> = actions
}