package net.doemges.kogniswarm.config

import kotlinx.coroutines.channels.Channel
import net.doemges.kogniswarm.io.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ShellConfig {
    @Bean
    fun messageChannel(): Channel<Request<String>> = Channel(1024)
}