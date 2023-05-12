package net.doemges.kogniswarm.config

import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.agent.AgentManager
import net.doemges.kogniswarm.agent.AgentMessageProcessor
import net.doemges.kogniswarm.assistant.AssistantProcessor
import net.doemges.kogniswarm.assistant.model.AssistantRequest
import net.doemges.kogniswarm.assistant.model.AssistantResponse
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.discord.DiscordInputProcessor
import net.doemges.kogniswarm.discord.DiscordOutputProcessor
import net.doemges.kogniswarm.discord.model.DiscordRequest
import net.doemges.kogniswarm.discord.model.DiscordResponse
import net.doemges.kogniswarm.io.InputGateway
import net.doemges.kogniswarm.io.OutputGateway
import net.doemges.kogniswarm.memory.MemoryService
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
        architectureBuilder: ArchitectureBuilder,
        agentMessageProcessor: AgentMessageProcessor
    ): Architecture = createComponent(architectureBuilder) {
        messageProcessor(agentMessageProcessor)
    } as Architecture


    @Bean
    fun architectureBuilder() = ArchitectureBuilder("KogniSwarm")

    @Bean
    fun agentMessageProcessor(
        discordInputGateway: InputGateway<DiscordRequest, DiscordResponse>,
        agentManager: AgentManager
    ) = AgentMessageProcessor(
        id = "agentMessageProcessor",
        input = discordInputGateway.input(),
        agentManager = agentManager
    )

    @Bean
    fun agentManager(
        discordOutputGateway: OutputGateway<DiscordRequest, DiscordResponse>,
        assistantOutputGateway: OutputGateway<AssistantRequest, AssistantResponse>,
        objectMapper: ObjectMapper,
        memoryService: MemoryService
    ): AgentManager = AgentManager(
        assistant = assistantOutputGateway.output(),
        output = discordOutputGateway.output(),
        objectMapper = objectMapper,
        memoryService = memoryService
    )

    @Bean
    fun assistantOutputGateway(chatService: ChatService, architectureBuilder: ArchitectureBuilder) =
        architectureBuilder.outputGateway<AssistantRequest, AssistantResponse>("assistant") {
            outputProcessor = AssistantProcessor(chatService)
        }

    @Bean
    fun discordOutputGateway(jda: JDA, architectureBuilder: ArchitectureBuilder) =
        architectureBuilder.outputGateway<DiscordRequest, DiscordResponse>("discordOutput") {
            outputProcessor = DiscordOutputProcessor(jda)
        }

    @Bean
    fun discordInputGateway(jda: JDA, architectureBuilder: ArchitectureBuilder) =
        architectureBuilder.inputGateway<DiscordRequest, DiscordResponse>("discordInput") {
            inputProcessor = DiscordInputProcessor(jda)
        }


}







