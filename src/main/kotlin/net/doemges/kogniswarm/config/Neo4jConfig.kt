package net.doemges.kogniswarm.config

import net.doemges.kogniswarm.memory.GenericNeo4JContainer
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.data.neo4j.core.Neo4jTemplate
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = ["net.doemges.kogniswarm.memory"])
class Neo4jConfig(private val neo4jProperties: Neo4jProperties) {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun neo4jDriver(neo4jContainer: GenericNeo4JContainer): Driver = GraphDatabase.driver(
        neo4jContainer.boltUrl,
        AuthTokens.basic("neo4j", neo4jContainer.adminPassword)
    )

    @Bean
    fun transactionManager(driver: Driver): Neo4jTransactionManager = Neo4jTransactionManager(driver)

    @Bean
    fun neo4jClient(driver: Driver): Neo4jClient = Neo4jClient.create(driver)

    @Bean
    fun neo4jTemplate(neo4jClient: Neo4jClient): Neo4jTemplate = Neo4jTemplate(neo4jClient)

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun neo4jContainer(): GenericNeo4JContainer {
        if (containerInstance != null) {
            return containerInstance!!
        }

        val hostDataDirectory = "src/main/resources/memory" // Change this to a folder on your host machine
        val containerDataDirectory = "/data"

        containerInstance = GenericNeo4JContainer("neo4j:5.7.0")
                .withAdminPassword(neo4jProperties.authentication.password)
                .apply {
                    addExposedPorts(7473, 7474, 7687)
                    withFileSystemBind(hostDataDirectory, containerDataDirectory) // Bind host folder to container folder
                    start()
                }

        return containerInstance!!
    }


    companion object {
        private var containerInstance: GenericNeo4JContainer? = null
    }
}
