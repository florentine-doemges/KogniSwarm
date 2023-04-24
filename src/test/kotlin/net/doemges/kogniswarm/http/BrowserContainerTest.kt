package net.doemges.kogniswarm.http

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.appmattus.kotlinfixture.kotlinFixture
import kotlinx.coroutines.runBlocking
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class BrowserContainerTest {

    private lateinit var container: BrowserContainer
    private val fixture = kotlinFixture()

    @BeforeEach
    fun setUp() {
        container = BrowserContainer.create()
    }

    @AfterEach
    fun tearDown() {
        container.stop()
    }


    @Test
    fun `browser container generates a unique ID`() = runBlocking {
        val otherContainer = BrowserContainer.create()

        assertThat(container.id).isNotEqualTo(otherContainer.id)

        otherContainer.stop()
    }

    @Test
    fun `browser container is started and can be stopped`() = runBlocking {
        assertThat(container.isRunning).isTrue()

        container.stop()

        await().atMost(Duration.ofSeconds(10)).until { !container.isRunning }

        assertThat(container.isRunning).isFalse()
    }
}
