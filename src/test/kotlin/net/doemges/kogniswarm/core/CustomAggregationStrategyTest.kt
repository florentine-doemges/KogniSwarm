package net.doemges.kogniswarm.core

import io.mockk.every
import io.mockk.mockk
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomAggregationStrategyTest {

    private lateinit var customAggregationStrategy: CustomAggregationStrategy

    @BeforeEach
    fun setup() {
        customAggregationStrategy = CustomAggregationStrategy()
    }

    @Test
    fun `should return newExchange if oldExchange is null`() {
        val newExchange: Exchange = mockk()

        val result = customAggregationStrategy.aggregate(null, newExchange)

        assertThat(result).isEqualTo(newExchange)
    }

    @Test
    fun `should return oldExchange if newExchange is null`() {
        val oldExchange: Exchange = mockk()

        val result = customAggregationStrategy.aggregate(oldExchange, null)

        assertThat(result).isEqualTo(oldExchange)
    }

    @Test
    fun `should merge headers if both exchanges are present`() {
        val oldExchange: Exchange = mockk()
        val newExchange: Exchange = mockk()
        val oldMessage: Message = mockk()
        val newMessage: Message = mockk()

        every { oldExchange.getIn() } returns oldMessage
        every { newExchange.getIn() } returns newMessage

        val oldHeaders = mutableMapOf<String, Any>()
        val newHeaders: MutableMap<String, Any> = mutableMapOf("newHeader" to "newValue")

        every { oldMessage.headers } returns oldHeaders
        every { newMessage.headers } returns newHeaders

        val result = customAggregationStrategy.aggregate(oldExchange, newExchange)

        assertThat(result).isEqualTo(oldExchange)
        assertThat(oldHeaders).containsEntry("newHeader", "newValue")
    }
}
