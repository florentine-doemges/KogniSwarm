package net.doemges.kogniswarm

import net.doemges.kogniswarm.core.Mission
import org.apache.camel.CamelContext
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class KogniSwarmRunner(
    private val camelContext: CamelContext
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {

        camelContext.createProducerTemplate()
            .requestBody(
                "direct:prompt",
                Mission(
                    "user",
                    "agent",
                    "Write me a book about digital marxism"
                )
            )

    }
}

