package net.doemges.kogniswarm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableConfigurationProperties(Neo4jProperties::class)
class KogniSwarmApplication {

}

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    DebugProbes.install()
    runApplication<KogniSwarmApplication>(*args)
}
