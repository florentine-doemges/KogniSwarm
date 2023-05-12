package net.doemges.kogniswarm.agent.model

import net.dv8tion.jda.api.entities.Message

data class AgentRequest(val content: String, val message: Message)