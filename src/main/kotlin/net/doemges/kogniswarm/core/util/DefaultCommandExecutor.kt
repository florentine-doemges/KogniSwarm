package net.doemges.kogniswarm.core.util

import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.SequenceInputStream

@Component
class DefaultCommandExecutor : CommandExecutor {
    @Suppress("DEPRECATION")
    override fun executeCommand(command: String): String = Runtime.getRuntime()
        .exec(command)
        .let { process ->
            BufferedReader(
                InputStreamReader(
                    SequenceInputStream(
                        process.inputStream,
                        process.errorStream
                    )
                )
            ).readText()
        }
}