package net.doemges.kogniswarm.command

abstract class BaseCommand(
    override val name: String, //name of the command
    override val description: String, //description of the command
    override val args: Map<String, String> //description of the arguments (key = argument name, value = argument description)
) : Command {

    //Returns a condensed description with name, description and arguments
    override fun toString(): String = "$name: $description\n${
        args.map { "  ${it.key}: ${it.value}" }
            .joinToString("\n")
    }"

}