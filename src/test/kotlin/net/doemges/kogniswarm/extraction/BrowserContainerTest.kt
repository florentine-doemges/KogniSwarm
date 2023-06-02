package net.doemges.kogniswarm.extraction

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import net.doemges.kogniswarm.extraction.util.BrowserContainer
import org.junit.jupiter.api.Test

class BrowserContainerTest {

    @Test
    fun `create should return a new BrowserContainer instance`() {
        val container = BrowserContainer.create()
        assertThat(container).isNotNull()
        assertThat(container).isInstanceOf(BrowserContainer::class.java)
        assertThat{ container.stop() }.isSuccess()
    }

}
