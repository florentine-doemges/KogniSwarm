package net.doemges.kogniswarm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.boot.ApplicationRunnerAutoConfiguration
import org.springframework.shell.boot.CompleterAutoConfiguration
import org.springframework.shell.boot.ShellRunnerAutoConfiguration
import org.springframework.shell.boot.SpringShellAutoConfiguration

@SpringBootApplication(
    exclude = [
        SpringShellAutoConfiguration::class,
        ApplicationRunnerAutoConfiguration::class,
        CompleterAutoConfiguration::class,
        ShellRunnerAutoConfiguration::class
    ]
)
class KogniSwarmApplication {
}

fun main(args: Array<String>) {
    runApplication<KogniSwarmApplication>(*args)
}
