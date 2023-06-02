package net.doemges.kogniswarm.core

import io.mockk.every
import io.mockk.mockk
import net.doemges.kogniswarm.core.util.LogExchangeFormatter
import org.apache.camel.Exchange
import org.apache.camel.ExchangePattern
import org.apache.camel.MessageHistory
import org.apache.camel.NamedNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogExchangeFormatterTest {
    private lateinit var formatter: LogExchangeFormatter
    private lateinit var exchange: Exchange

    @BeforeEach
    fun setup() {
        formatter = LogExchangeFormatter()
        exchange = mockk(relaxed = true)
    }

    @Test
    fun `it formats an exchange correctly`() {
        // Arrange
        val exchangeId = "123"
        val exchangePattern = "InOut"
        val properties = mapOf("prop1" to "value1", "prop2" to "value2")
        val messageHistory = listOf(mockk<MessageHistory>().apply {
            every { node } returns mockk<NamedNode>().apply {
                every { id } returns "nodeId"
            }
            every { routeId } returns "routeId"
        })
        val headers = mapOf("header1" to "value1", "header2" to "value2")
        val body = "testBody"
        val bodyClass = body::class.qualifiedName

        every { exchange.exchangeId } returns exchangeId
        every { exchange.pattern } returns ExchangePattern.valueOf(exchangePattern)
        every { exchange.properties } returns properties
        every { exchange.getProperty(Exchange.MESSAGE_HISTORY, List::class.java) } returns messageHistory
        every { exchange.`in`.headers } returns headers
        every { exchange.`in`.body } returns body

        // Act
        val result = formatter.format(exchange)

        // Assert
        assertThat(result).contains("Exchange ID: $exchangeId")
        assertThat(result).contains("Exchange Pattern: $exchangePattern")
        properties.forEach { (key, value) ->
            assertThat(result).contains("$key = $value")
        }
        assertThat(result).contains("Message History:")
        assertThat(result).contains("Headers:")
        headers.forEach { (key, value) ->
            assertThat(result).contains("$key = $value")
        }
        assertThat(result).contains("Body Type: $bodyClass")
        assertThat(result).contains("Body: $body")
    }
}
