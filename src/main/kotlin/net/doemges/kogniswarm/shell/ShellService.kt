package net.doemges.kogniswarm.shell

import org.jline.terminal.Terminal
import org.springframework.shell.ResultHandler
import org.springframework.shell.ResultHandlerService
import org.springframework.shell.Shell
import org.springframework.shell.command.CommandCatalog
import org.springframework.shell.config.ShellConversionServiceSupplier
import org.springframework.shell.context.ShellContext
import org.springframework.shell.exit.ExitCodeMappings
import org.springframework.shell.result.GenericResultHandlerService
import org.springframework.stereotype.Service

@Service
class ShellService(
    val commandRegistry: CommandCatalog,
    val terminal: Terminal,
    val shellContext: ShellContext,
    val exitCodeMappings: ExitCodeMappings,
    val shellConversionServiceSupplier: ShellConversionServiceSupplier
) {
    fun <T> createShell(vararg resultHandlers: ResultHandler<T>?): Shell = Shell(
        createResultHandlerService(resultHandlers.filterNotNull()),
        commandRegistry,
        terminal,
        shellContext,
        exitCodeMappings
    ).apply {
        setConversionService(shellConversionServiceSupplier.get())
    }

    private fun <T> createResultHandlerService(resultHandlers: List<ResultHandler<T>>): ResultHandlerService =
        GenericResultHandlerService().apply { resultHandlers.forEach { addResultHandler(it) } }

}