package net.doemges.kogniswarm.agent.model

import net.doemges.kogniswarm.agent.AgentIdentifier

data class AgentResponse(val response: String, val identifier: AgentIdentifier, val request: AgentRequest)