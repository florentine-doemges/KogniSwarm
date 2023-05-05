package net.doemges.kogniswarm.shell

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import org.jline.builtins.ConfigurationPath
import org.jline.console.impl.SystemRegistryImpl
import org.jline.reader.Parser
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class ShellService(
    private val shellChannel: Channel<Request<ShellTask>>
) {

    private lateinit var systemRegistry: SystemRegistryImpl

    private val scope = CoroutineScope(Dispatchers.IO)

    @PostConstruct
    fun setup() {
        initializeJLine()
        scope.launch {
            for (request in shellChannel) {
                val shellTask = request.message
                execute(shellTask)
                request.response.send(Response(shellTask))
            }
        }
    }

    private fun execute(shellTask: ShellTask) {
        try {
            val result = systemRegistry.execute(shellTask.command)
            shellTask.also { it.result = result }
        } catch (e: Throwable) {
            shellTask.also { it.exception = e }
        }
    }

    private fun initializeJLine() {
        val terminal: Terminal = TerminalBuilder.builder()
                .system(true)
                .build()
        val parser: Parser = DefaultParser()

        // Initialize commandRegistries array with a default CommandRegistry
        val appConfig = Paths.get("src/main/resources")
        val userConfig = Paths.get("src/main/resources")
        val configPath = ConfigurationPath(appConfig, userConfig) // Provide appConfig and userConfig
        val commandRegistry = CustomCommandRegistry()
        systemRegistry = SystemRegistryImpl(parser, terminal, { Paths.get("") }, configPath)
        systemRegistry.setCommandRegistries(commandRegistry)
    }


}

data class ShellTask(val command: String, var result: Any? = null, var exception: Throwable? = null)

