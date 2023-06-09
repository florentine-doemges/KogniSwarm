package net.doemges.kogniswarm.template

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isSuccess
import com.aallam.openai.api.BetaOpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.openai.service.OpenAIChatCompletionService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import com.fasterxml.jackson.core.type.TypeReference

@SpringBootTest
class TemplateIntegrationTest {

    @Autowired
    private lateinit var openAIChatCompletionService: OpenAIChatCompletionService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @OptIn(BetaOpenAI::class)
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
                postProcessor(openAIChatCompletionService.simpleChatCompletion)
            }
            .build()

        assertThat {
            template.replace()
                .also {
                    println(it)
                }
        }.isSuccess()
            .isNotEmpty()


    }

    @Test
    fun testComplexTemplate() = runBlocking {

        val prompt = """Please fill in the blanks with your own words. For every blank, use a unique variable name in curly brackets {}:
            |"{Adjective1} {Noun1} {Verb1} {Adverb} when {Adjective2} {Noun2}s {Verb2} {Preposition} {Adjective3} {Noun3}s."
            |
            |Next, provide a list of replacements for each variable, written as "{Variable}": "{Replacement}". For example, if you chose {Adjective1} as a variable, you might replace it with "Funny".
            |
            |Please make sure all your variables match exactly between the template and the replacements. Also, avoid adding extra characters or comments. Your answer should look like this:
            |```
            |{
            |   "template":"{Your} {Variables} {Here}",
            |   "variables":{
            |       "Your":"Your Replacement",
            |       "Variables":"Variables Replacement",
            |       "Here":"Here Replacement"
            |   }
            |}
            |
            |Please replace Your, Variables, and Here with your own words, and do the same for their replacements.""".trimMargin()
        println(prompt)
        val result = openAIChatCompletionService.simpleChatCompletion(
            prompt
        )

        println(result)

        val parsed = objectMapper.readValue(result, object : TypeReference<Map<String, Any>>() {})

        val template = Template
            .builder {
                encodedTemplate(parsed["template"] as String)
                parsed["variables"]?.let {
                    @Suppress("UNCHECKED_CAST") val variables = it as Map<String, String>
                    variables.forEach { (variable, replacement) ->
                        replacement(variable, replacement)
                    }
                }
            }
            .build()

        assertThat {
            template.replace()
                .also {
                    println(it)
                }
        }.isSuccess()
            .isNotEmpty()


    }
}
