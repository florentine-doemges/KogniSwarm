package net.doemges.kogniswarm.config

import kotlinx.coroutines.channels.Channel
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.shell.ShellTask
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ShellConfig {
    @Bean
    fun shellChannel(): Channel<Request<ShellTask>> = Channel(1024)

}