package net.doemges.kogniswarm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
class KogniSwarmApplication

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<KogniSwarmApplication>(*args)
}
