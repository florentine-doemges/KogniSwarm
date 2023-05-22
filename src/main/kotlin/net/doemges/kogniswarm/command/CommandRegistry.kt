package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class CommandRegistry(private val commands: List<Command>) {
    fun getCommandByName(name: String): Command =
        commands.find { it.name == name } ?: error("Command $name not found.")

    @Suppress("UNCHECKED_CAST")
    fun <T : Command> getCommandByClass(clazz: Class<T>): T =
        commands.find { it.javaClass == clazz } as? T ?: error("Command ${clazz.simpleName} not found.")

    final inline fun <reified T : Command> getCommand(): T = getCommandByClass(T::class.java)

}