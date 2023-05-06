package net.doemges.kogniswarm.shell

import org.jline.console.CmdDesc
import org.jline.console.CommandRegistry
import org.jline.reader.impl.completer.SystemCompleter

class CustomCommandRegistry(commands: List<Command> = emptyList()) : CommandRegistry {

    private val commands: Map<String, Command> = commands
            .associateBy { it.name }
            .toMap()

    override fun commandNames(): MutableSet<String> = commands
            .keys
            .toMutableSet()

    override fun commandAliases(): MutableMap<String, String> = commands
            .map { it.key to it.value.name }
            .toMap()
            .toMutableMap()

    override fun commandInfo(command: String): List<String> = commands[command]
            ?.info
            ?.split("\n")
        ?: mutableListOf()

    override fun hasCommand(command: String): Boolean =
        commands.containsKey(command) || commandAliases().containsKey(command)

    override fun compileCompleters(): SystemCompleter = SystemCompleter()

    override fun commandDescription(args: MutableList<String>): CmdDesc = CmdDesc()
}



data class Command(val name: String, val info: String)
