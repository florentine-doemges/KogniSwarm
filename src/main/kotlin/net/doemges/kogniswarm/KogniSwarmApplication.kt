package net.doemges.kogniswarm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KogniSwarmApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<KogniSwarmApplication>(*args)
}
