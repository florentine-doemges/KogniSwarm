package net.doemges.kogniswarm.tool.service

import assertk.all
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isSuccess
import assertk.assertions.size
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GoogleSearchApiClientServiceTest {

    @Autowired
    private lateinit var googleSearchApiClientService: GoogleSearchApiClientService

    @Test
    fun testSearch() = runBlocking {
        assertThat {
            googleSearchApiClientService.fetchItems("Modbus Documentation", 1, 10)
                .toCollection(mutableListOf())
        }.isSuccess().all {
            hasSize(10)
        }
    }
    @Test
    fun testSearchWithPaging() = runBlocking {
        assertThat {
            googleSearchApiClientService.fetchItems("Modbus Documentation", 11, 10)
                .toCollection(mutableListOf())
        }.isSuccess().all {
            hasSize(10)
        }
    }
}