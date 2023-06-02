package net.doemges.kogniswarm.core.util

interface CommandExecutor {
    fun executeCommand(command: String): String
}