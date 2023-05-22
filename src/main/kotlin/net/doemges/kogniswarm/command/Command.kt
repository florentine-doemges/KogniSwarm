package net.doemges.kogniswarm.command

interface Command {
    val name: String
    val description: String
    val args: Map<String, String>
    fun execute(commandInput: CommandInput): CommandOutput
}

open class CommandBuilder {
    private val args: MutableMap<String, String> = mutableMapOf()

    fun arg(key: String, value: String) = apply {
        args[key] = value
    }
    inline fun <reified T: CommandOutputBuilder> output(noinline init: T.() -> Unit): T {
        val command = T::class.java.newInstance()
        command.init()
        return command
    }
}

interface CommandInput{
    val args: Map<String, List<String>>
}

open class CommandOutput(
    val input: CommandInput,
    val result: Any?
)

interface CommandOutputBuilder

