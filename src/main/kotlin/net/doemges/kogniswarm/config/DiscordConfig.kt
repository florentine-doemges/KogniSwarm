package net.doemges.kogniswarm.config

import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfig {
    @Bean
    fun jda(@Value("\${discord.token}") token: String): JDA = runBlocking {
        JDABuilder.createDefault(token)
                .build()
                .awaitReady()
    }
}