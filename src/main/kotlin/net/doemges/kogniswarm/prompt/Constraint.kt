package net.doemges.kogniswarm.prompt

data class Constraint(val text: String){
    override fun toString(): String = text
}

@Suppress("MemberVisibilityCanBePrivate")
class Constraints private constructor() {
    companion object {
        fun memoryLimit() = Constraint(
            """~4000 word limit for short term memory. 
                |Your short term memory is short, so immediately save important information to files.""".trimMargin()
        )

        fun rememberPastEvents() = Constraint(
            """If you are unsure how you previously did something or want to recall past events, 
                    |thinking about similar events will help you remember""".trimMargin()
        )

        fun exclusiveCommandList() = Constraint("Exclusively use the commands listed below e.g. command_name")


        fun noUserAssistance() = Constraint("No user assistance")

        fun standardConstraints(): List<Constraint> =
            listOf(memoryLimit(), rememberPastEvents(), noUserAssistance(), exclusiveCommandList())
    }
}