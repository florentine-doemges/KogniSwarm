package net.doemges.kogniswarm.config

import CustomScriptEngine
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.channels.Channel
import net.doemges.kogniswarm.io.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ShellConfig {
    @Bean
    fun messageChannel(): Channel<Request<String>> = Channel(1024)

    @Bean
    fun unparseablesChannel(): Channel<Request<String>> = Channel(1024)
    @Bean
    fun customScriptEngine(objectMapper: ObjectMapper): CustomScriptEngine = CustomScriptEngine(objectMapper)
}