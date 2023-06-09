package net.doemges.kogniswarm.mission.service

import kotlinx.coroutines.reactor.awaitSingle
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.agent.service.AgentManagementService
import net.doemges.kogniswarm.mission.model.Mission
import net.doemges.kogniswarm.mission.model.MissionKey
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Service

@Service
class MissionManagementService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val agentManagementService: AgentManagementService
) {
    suspend fun createMission(userPrompt: String, userName: String): Mission =
        runCatching { findMission(userName, userPrompt) }
            .getOrElse { null }
            ?.also { error("This mission already exists for this user!") }
            ?: createMissionInternal(userName, userPrompt)

    @Suppress("unused")
    suspend fun createMission(key: MissionKey): Mission = createMission(key.userPrompt, key.user)

    suspend fun updateMission(userPrompt: String, userName: String, block: Mission.() -> Unit = {}): Mission =
        findMission(userName, userPrompt)
            ?.apply(block)
            ?.also {
                reactiveMongoTemplate.save(it)
                    .awaitSingle()
            } ?: error("This mission does not exist for this user!")

    @Suppress("unused")
    suspend fun updateMission(key: MissionKey, block: Mission.() -> Unit = {}): Mission =
        updateMission(key.userPrompt, key.user, block)

    suspend fun addActionToMission(userPrompt: String, userName: String, action: Action): Mission =
        updateMission(userPrompt, userName) {
            actions.add(action)
        }



    private suspend fun createMissionInternal(userName: String, userPrompt: String): Mission =
        agentManagementService.getAgent()
            .let { agent ->
                Mission(
                    missionKey = MissionKey(
                        user = userName,
                        agentName = agent.name,
                        userPrompt = userPrompt
                    ),
                    agent = agent
                ).apply {
                    reactiveMongoTemplate.save(this)
                        .awaitSingle()
                }
            }

    private suspend fun findMission(
        userName: String,
        userPrompt: String
    ): Mission? = reactiveMongoTemplate.findOne(
        query(
            where("userName")
                .`is`(userName)
                .and("userPrompt")
                .`is`(userPrompt)
        ), Mission::class.java
    )
        .awaitSingle()
}
