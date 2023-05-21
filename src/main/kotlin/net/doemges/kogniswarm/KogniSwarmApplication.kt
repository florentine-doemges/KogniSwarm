package net.doemges.kogniswarm

import io.github.florentine_doemges.camel_discord.JDAComponent
import io.github.florentine_doemges.camel_discord.config.JDAConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(JDAConfig::class, JDAComponent::class)
class KogniSwarmApplication

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    DebugProbes.install()
    runApplication<KogniSwarmApplication>(*args)
}
