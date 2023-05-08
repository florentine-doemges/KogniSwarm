package net.doemges.kogniswarm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import net.doemges.kogniswarm.config.Neo4jProperties
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(Neo4jProperties::class)
class KogniSwarmApplication {

    fun keepAliveRunner(): CommandLineRunner {
        return CommandLineRunner {
            val lock = Object()
            synchronized(lock) {
                while (true) {
                    lock.wait()
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    DebugProbes.install()
    runApplication<KogniSwarmApplication>(*args)
}
