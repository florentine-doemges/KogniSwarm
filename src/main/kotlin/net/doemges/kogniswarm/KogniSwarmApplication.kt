package net.doemges.kogniswarm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KogniSwarmApplication{
}

fun main(args: Array<String>) {

    runApplication<KogniSwarmApplication>(*args)
}
