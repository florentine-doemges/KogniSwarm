package net.doemges.kogniswarm

import dev.kord.core.Kord
import org.springframework.boot.CommandLineRunner
import org.springframework.shell.Shell
import org.springframework.stereotype.Component

@Component
class KogniSwarmRunner(
    private val kord: Kord
) : CommandLineRunner {
    override fun run(vararg args: String?) {

    }
}
