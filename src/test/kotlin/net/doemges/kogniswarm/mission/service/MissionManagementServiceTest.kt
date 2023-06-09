package net.doemges.kogniswarm.mission.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MissionManagementServiceTest {

    @Autowired
    private lateinit var missionManagementService: MissionManagementService

    @Test
    fun testMissionManagementService() = runBlocking{
        missionManagementService.createMission("Write me a book about digital marxism", "Karl Marx")
    }
}