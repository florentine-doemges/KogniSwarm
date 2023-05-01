package net.doemges.kogniswarm.config

import dev.kord.core.Kord
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.shell.InputProvider

@Configuration
class DiscordConfig {
    @Bean
    fun kord(@Value("\${discord.token}") token: String): Kord = runBlocking {
        Kord(token).apply { login() }
    }
}

