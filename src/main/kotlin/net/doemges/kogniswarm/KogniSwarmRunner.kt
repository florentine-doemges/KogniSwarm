package net.doemges.kogniswarm

import net.doemges.kogniswarm.mission.model.MissionKey
import org.apache.camel.CamelContext
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile

//@Component
@Profile("!test")
class KogniSwarmRunner(
    private val camelContext: CamelContext,
    private val context: ApplicationContext
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun run(args: ApplicationArguments?) {
        if (logger.isDebugEnabled){
            val beanNames = context.beanDefinitionNames
            beanNames.sort()
            for (beanName in beanNames) {
                val beanType = context.getType(beanName)
                logger.debug("Bean Name: $beanName, Bean Type: $beanType")
            }
        }


        camelContext.createProducerTemplate()
            .requestBody(
                "direct:prompt",
                MissionKey(
                    "user",
                    "agent",
                    "Write me a book about digital marxism"
                )
            )

    }
}

