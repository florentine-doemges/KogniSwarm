package net.doemges.kogniswarm.docker

import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class DefaultCommandExecutor : CommandExecutor {
    override fun executeCommand(command: String): String {
        @Suppress("DEPRECATION") val process = Runtime.getRuntime()
            .exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        return reader.readText()
    }
}