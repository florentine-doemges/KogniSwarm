package net.doemges.kogniswarm.agent.service

import org.springframework.stereotype.Service

@Service
class AgentManagementService {
    fun getAgent(): Agent{
        return Agent("test")
    }

}

class Agent(val name: String) {

}
