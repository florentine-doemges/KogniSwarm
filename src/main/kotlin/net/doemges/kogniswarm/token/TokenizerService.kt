package net.doemges.kogniswarm.token

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class TokenizerService(webClient: WebClient) {
    val tokenizer = Tokenizer("gpt-3.5-turbo", webClient)
    fun tokenize(input: String): List<String> = tokenizer.tokenize(input)
}