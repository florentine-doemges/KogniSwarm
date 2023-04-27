package net.doemges.kogniswarm.chat

import assertk.all
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import assertk.assertions.prop
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

@SpringBootTest
class CodeGeneratorTest {

    private val input: ChatMessageBundle =
        ChatMessageBundle.fromInput(
            "Create a simple function to add two numbers in Kotlin.",
            "Do not output anything but the code because your answer will be checked by a compiler."
        )

    @Autowired
    private lateinit var chatService: ChatService

    private lateinit var generatedCode: String

    @Test
    fun `Generated code should be syntactically correct and functional Kotlin code`() {
        whenUserGeneratesCode()
        thenOutputShouldBeCorrectKotlinCode()
    }

    private fun whenUserGeneratesCode() {
        generatedCode = chatService.sendToChatGpt(input)
    }

    private fun thenOutputShouldBeCorrectKotlinCode() {
        val compilationConfiguration = ScriptCompilationConfiguration {
            jvm {
                dependenciesFromClassContext(
                    CodeGeneratorTest::class,
                    wholeClasspath = true
                )
            }
        }

        val evaluationConfiguration = ScriptEvaluationConfiguration {}

        assertThat {
            runBlocking {
                BasicJvmScriptingHost().eval(
                    generatedCode.toScriptSource(),
                    compilationConfiguration,
                    evaluationConfiguration
                )
            }
        }.isSuccess().all {
            isInstanceOf(ResultWithDiagnostics.Success::class.java)
            prop(ResultWithDiagnostics<EvaluationResult>::reports).isNotNull()
        }
    }
}
