package net.doemges.kogniswarm.action

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNullOrEmpty
import assertk.assertions.isSuccess
import io.mockk.mockk
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.action.service.ActionHistoryService
import net.doemges.kogniswarm.mission.model.MissionKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActionHistoryTest {

    private lateinit var actionHistoryService: ActionHistoryService

    @BeforeEach
    fun setUp() {
        actionHistoryService = ActionHistoryService()
    }

    @Test
    fun `put adds action to history`() {
        val missionKey = MissionKey("user1", "agent1", "prompt1")
        val action = Action(mockk(), emptyMap())

        actionHistoryService.clear()

        actionHistoryService.put(missionKey, action)

        assertThat { actionHistoryService.get(missionKey) }
            .isSuccess()
            .all {
                isNotNull()
                isEqualTo(listOf(action))
            }
    }

    @Test
    fun `get returns null for non-existent mission`() {
        val missionKey = MissionKey("user1", "agent1", "prompt1")

        actionHistoryService.clear()

        assertThat { actionHistoryService.get(missionKey) }
            .isSuccess()
            .isNullOrEmpty()
    }

    @Test
    fun `get returns all actions for a mission`() {
        val missionKey = MissionKey("user1", "agent1", "prompt1")
        val action1 = Action(mockk(), emptyMap())
        val action2 = Action(mockk(), emptyMap())

        actionHistoryService.clear()

        actionHistoryService.put(missionKey, action1)
        actionHistoryService.put(missionKey, action2)

        assertThat { actionHistoryService.get(missionKey) }
            .isSuccess()
            .all {
                isNotNull()
                isEqualTo(listOf(action1, action2))
            }
    }
}
