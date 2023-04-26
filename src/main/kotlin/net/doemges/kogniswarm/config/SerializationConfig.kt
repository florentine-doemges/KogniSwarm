package net.doemges.kogniswarm.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SerializationConfig {

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    }
}
