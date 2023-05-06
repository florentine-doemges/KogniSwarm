package net.doemges.kogniswarm

import net.doemges.kogniswarm.config.Neo4jProperties
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(Neo4jProperties::class)
class KogniSwarmApplication {

//    @Bean
//    @Profile("dev")
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

fun main(args: Array<String>) {

    runApplication<KogniSwarmApplication>(*args)

}
