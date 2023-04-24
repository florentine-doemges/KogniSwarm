package net.doemges.kogniswarm.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SerializationConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
