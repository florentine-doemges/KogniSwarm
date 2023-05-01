package net.doemges.kogniswarm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@SpringBootApplication
@ShellComponent
class KogniSwarmApplication{
    @ShellMethod
    fun hi(): String {
        return "hi"
    }
}

fun main(args: Array<String>) {

    runApplication<KogniSwarmApplication>(*args)
}
