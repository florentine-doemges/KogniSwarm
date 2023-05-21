package net.doemges.kogniswarm.config

import com.aallam.openai.client.OpenAI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAIConfig(@Value("\${openai.api.key}") private val token: String) {
    @Bean
    fun openAI(): OpenAI = OpenAI(token)
}