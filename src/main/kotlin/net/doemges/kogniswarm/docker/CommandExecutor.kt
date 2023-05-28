package net.doemges.kogniswarm.docker

interface CommandExecutor {
    fun executeCommand(command: String): String
}