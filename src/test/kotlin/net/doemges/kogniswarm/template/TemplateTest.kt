package net.doemges.kogniswarm.template

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TemplateTest {

    @Test
    fun testTemplate() = runBlocking {
        val template = Template
            .builder {
                encodedTemplate("Hello, {name}. You have {count} new messages. {{innerTemplate}}")
                replacement("name", "John")
                replacement("count", "5")
                template("innerTemplate") {
                    encodedTemplate("Your balance is {balance}.")
                    replacement("balance", "1000")
                }
            }
            .build()

        val result = template.replace()

        assertEquals("Hello, John. You have 5 new messages. Your balance is 1000.", result)
    }

    @Test
    fun testTemplateWithPostProcessing() = runBlocking {

        val template = Template
            .builder {
                encodedTemplate("Hello, {name}. You have {count} new messages. {{innerTemplate}}")
                replacement("name", "John")
                replacement("count", "5")
                template("innerTemplate") {
                    encodedTemplate("Your balance is {balance}.")
                    replacement("balance", "1000")
                    postProcessor { Regex("[0-9]+").replace(it, "YYY") }
                }
                postProcessor { Regex("[0-9]+").replace(it, "XXX") }
            }
            .build()

        val result = template.replace()

        assertEquals("Hello, John. You have XXX new messages. Your balance is YYY.", result)
    }
}
