package net.doemges.kogniswarm.shell

import CustomPrinter
import CustomScriptEngine
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import org.jline.builtins.ConfigurationPath
import org.jline.console.impl.ConsoleEngineImpl
import org.jline.console.impl.SystemRegistryImpl
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.Parser
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class ShellService(
    private val messageChannel: Channel<Request<String>>,
    private val unparseablesChannel: Channel<Request<String>>,
    private val scriptEngine: CustomScriptEngine
) {

    private lateinit var systemRegistry: SystemRegistryImpl

    private val scope = CoroutineScope(Dispatchers.IO)

    @PostConstruct
    fun setup() {
        initializeJLine()
        scope.launch {
            for (request in messageChannel) {
                request.response.send(Response(executeCommand(request.message)))
            }
        }
    }

    private fun initializeJLine() {
        val terminal: Terminal = TerminalBuilder.builder()
                .system(true)
                .build()
        val parser: Parser = DefaultParser()

        // Initialize commandRegistries array with a default CommandRegistry
        val printer = CustomPrinter()
        val appConfig = Paths.get("src/main/resources")
        val userConfig = Paths.get("src/main/resources")
        val configPath = ConfigurationPath(appConfig, userConfig) // Provide appConfig and userConfig
        val commandRegistry = ConsoleEngineImpl(scriptEngine, printer, { Paths.get("") }, configPath)
        systemRegistry = SystemRegistryImpl(parser, terminal, { Paths.get("") }, configPath)
        systemRegistry.setCommandRegistries(commandRegistry)

        val lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(parser)
                .completer(systemRegistry.completer())
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
                .build()
        commandRegistry.setLineReader(lineReader)
    }


    private fun executeCommand(message: String): String = try {
        systemRegistry.execute(message)
    } catch (e: Exception) {
        val responseChannel: Channel<Response<String>> = Channel()
        val request = Request(message, responseChannel)

        val response = runBlocking {
            unparseablesChannel.send(request)
            responseChannel.receive()
        }
        response.message
    }.toString()
}


