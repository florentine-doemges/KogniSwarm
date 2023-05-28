package net.doemges.kogniswarm.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds

@Configuration
class OpenAIConfig(@Value("\${openai.api.key}") private val token: String) {
    @Bean
    fun openAI(): OpenAI = OpenAI(OpenAIConfig(token = token, timeout = Timeout(socket = 60.seconds)))


}