package net.doemges.kogniswarm.think

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.camel.CamelContext
import org.apache.camel.Message
import org.springframework.stereotype.Component

@Component
class EndOfActionDecisionProcessor(camelContext: CamelContext, objectMapper: ObjectMapper) : AbstractThinkingProcessor(camelContext, objectMapper) {
    override val promptTemplate: String = """The goal is: '{goal}'
            Given the following context:
            {context}
            And your action history:
            {actionHistory}
            And your last action:
            {action}
            
            
            Should the mission be continued to achieve the goal or has it already been achieved? 
            Please answer just 'YES' => continue or 'NO'. => do not continue
            """.trimIndent()

    override fun processResponse(message: Message, responseText: String?) {
        message.headers["shouldContinue"] = !responseText?.uppercase()
            ?.contains("NO")!!
    }
}


