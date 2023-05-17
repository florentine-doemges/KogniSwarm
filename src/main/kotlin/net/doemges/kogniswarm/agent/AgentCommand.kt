package net.doemges.kogniswarm.agent

data class AgentCommand(
    val name: String,
    val description: String,
    val args: List<Pair<String, String>> = listOf(),
    val block: Agent.(Map<String, String>) -> String = { "Command $name not implemented" }
) {
    override fun toString(): String = "$name: $description\n${
        args.joinToString("\n") { "  ${it.first}: ${it.second}" }
    }"
}