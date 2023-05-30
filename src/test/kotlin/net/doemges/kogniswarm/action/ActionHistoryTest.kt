package net.doemges.kogniswarm.action

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNullOrEmpty
import assertk.assertions.isSuccess
import io.mockk.mockk
import net.doemges.kogniswarm.core.Mission
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActionHistoryTest {

    private lateinit var actionHistory: ActionHistory

    @BeforeEach
    fun setUp() {
        actionHistory = ActionHistory()
    }

    @Test
    fun `put adds action to history`() {
        val mission = Mission("user1", "agent1", "prompt1")
        val action = Action(mockk(), emptyMap())

        actionHistory.clear()

        actionHistory.put(mission, action)

        assertThat { actionHistory.get(mission) }
            .isSuccess()
            .all {
                isNotNull()
                isEqualTo(listOf(action))
            }
    }

    @Test
    fun `get returns null for non-existent mission`() {
        val mission = Mission("user1", "agent1", "prompt1")

        actionHistory.clear()

        assertThat { actionHistory.get(mission) }
            .isSuccess()
            .isNullOrEmpty()
    }

    @Test
    fun `get returns all actions for a mission`() {
        val mission = Mission("user1", "agent1", "prompt1")
        val action1 = Action(mockk(), emptyMap())
        val action2 = Action(mockk(), emptyMap())

        actionHistory.clear()

        actionHistory.put(mission, action1)
        actionHistory.put(mission, action2)

        assertThat { actionHistory.get(mission) }
            .isSuccess()
            .all {
                isNotNull()
                isEqualTo(listOf(action1, action2))
            }
    }
}
