package net.doemges.kogniswarm.config

import dev.kord.core.Kord
import dev.kord.core.entity.Message
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.discord.EventWrapper
import net.doemges.kogniswarm.io.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfig {
    @Bean
    fun kord(@Value("\${discord.token}") token: String): Kord = runBlocking {
        Kord(token)
    }

    @Bean
    fun discordEventChannel(): Channel<Request<EventWrapper>> = Channel(1024)
}

