import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.shell.ScriptExecutionException
import org.jline.console.ScriptEngine
import org.jline.reader.Completer
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost


class CustomScriptEngine(private val objectMapper: ObjectMapper) : ScriptEngine {
    private val scriptingHost = BasicJvmScriptingHost()
    override fun toString(`object`: Any?): String = `object`?.toString() ?: ""


    override fun getEngineName(): String = "Kotlin Script Engine"

    override fun getExtensions(): MutableCollection<String> = mutableListOf("kts")


    override fun getScriptCompleter(): Completer =
        Completer { _, _, _ -> }

    override fun hasVariable(name: String?): Boolean = false

    override fun put(name: String?, value: Any?) {
        // Implement variable storage if needed
    }

    override fun get(name: String?): Any = Unit

    override fun find(name: String?): MutableMap<String, Any> =
        mutableMapOf()

    override fun del(vararg vars: String?) {
        // Implement variable deletion if needed
    }

    override fun toJson(`object`: Any?): String =
        "{}"

    override fun toMap(`object`: Any?): MutableMap<String, Any> =
        mutableMapOf()

    override fun deserialize(value: String?, format: String?): Any? = when (format) {
        "json" -> deserializeJsonToMap(value)
        else -> error("Unsupported format: $format")
    }

    @Suppress("UNCHECKED_CAST")
    private fun deserializeJsonToMap(jsonString: String?): Map<String, Any>? {
        if (jsonString == null) {
            error("JSON string is null")
        }

        if (jsonString.isEmpty()){
            return null
        }

        return objectMapper.readValue(jsonString, Map::class.java) as Map<String, Any>
    }
    override fun getSerializationFormats(): MutableList<String> = mutableListOf("json") // Add other formats if needed

    override fun getDeserializationFormats(): MutableList<String> =
        mutableListOf("json") // Add other formats if needed

    override fun persist(file: Path?, `object`: Any?) {
        // Implement object persistence if needed
    }

    override fun persist(file: Path?, `object`: Any?, format: String?) {
        // Implement object persistence with a specific format if needed
    }


    override fun execute(statement: String): Any {
        if (statement.isEmpty()) {
            throw IllegalArgumentException("Statement cannot be empty")
        }

        val scriptSource = statement.toScriptSource()

        val scriptCompilationConfiguration = createScriptCompilationConfiguration()
        val scriptEvaluationConfiguration = createScriptEvaluationConfiguration()

        val result = scriptingHost.eval(scriptSource, scriptCompilationConfiguration, scriptEvaluationConfiguration)

        return when (result) {
            is ResultWithDiagnostics.Success -> result.value.returnValue
            is ResultWithDiagnostics.Failure -> throw ScriptExecutionException(result.reports)
        }
    }

    override fun execute(script: File?, args: Array<out Any>?): Any = execute(script?.readText() ?: "")

    override fun execute(closure: Any?, vararg args: Any?): Any =
        // Implement execution of closures if needed
        Unit

    private fun createScriptCompilationConfiguration(): ScriptCompilationConfiguration =
        ScriptCompilationConfiguration {
            jvm {
                dependenciesFromClassContext(CustomScriptEngine::class, wholeClasspath = true)
            }
        }

    private fun createScriptEvaluationConfiguration(): ScriptEvaluationConfiguration = ScriptEvaluationConfiguration()
}
