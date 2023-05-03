package net.doemges.kogniswarm

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class KogniSwarmApplication {

    @Bean
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
