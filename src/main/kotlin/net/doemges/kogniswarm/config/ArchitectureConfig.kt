package net.doemges.kogniswarm.config

import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.agent.AgentMessageProcessor
import net.doemges.kogniswarm.assistant.AssistantProcessor
import net.doemges.kogniswarm.assistant.AssistantRequest
import net.doemges.kogniswarm.assistant.AssistantResponse
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.discord.DiscordInputProcessor
import net.doemges.kogniswarm.discord.DiscordOutputProcessor
import net.doemges.kogniswarm.discord.DiscordRequest
import net.doemges.kogniswarm.discord.DiscordResponse
import net.doemges.kogniswarm.structure.Architecture
import net.doemges.kogniswarm.structure.ArchitectureBuilder
import net.doemges.kogniswarm.structure.createComponent
import net.dv8tion.jda.api.JDA
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ArchitectureConfig {
    @Bean
    fun architecture(
        jda: JDA,
        chatService: ChatService,
        objectMapper: ObjectMapper
    ): Architecture =
        createComponent(ArchitectureBuilder("KogniSwarm")) {
            val discordInputGateway =
                inputGateway<DiscordRequest, DiscordResponse>("discordInput") {
                    inputProcessor = DiscordInputProcessor(jda)
                }
            val discordOutputGateway =
                outputGateway<DiscordRequest, DiscordResponse>("discordOutput") {
                    outputProcessor = DiscordOutputProcessor(jda)
                }
            val assistant =
                outputGateway<AssistantRequest, AssistantResponse>("assistant") {
                    outputProcessor = AssistantProcessor(chatService)
                }
            messageProcessor(
                AgentMessageProcessor(
                    id = "agentMessageProcessor",
                    input = discordInputGateway.input(),
                    output = discordOutputGateway.output(),
                    assistant = assistant.output(),
                    objectMapper = objectMapper
                )
            )
        } as Architecture
}






