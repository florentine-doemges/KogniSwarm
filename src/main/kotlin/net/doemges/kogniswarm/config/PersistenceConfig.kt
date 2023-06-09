package net.doemges.kogniswarm.config

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName


@Configuration
@EnableReactiveMongoRepositories
@Profile("!test")
class PersistenceConfig : AbstractReactiveMongoConfiguration() {
    @Bean(destroyMethod = "stop", initMethod = "start")
    fun mongoDBContainer(): MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
    override fun getDatabaseName(): String = "reactive"

    @Bean(destroyMethod = "close")
    fun mongoClient(mongoDBContainer: MongoDBContainer): MongoClient = MongoClients.create(
        ConnectionString(
            "mongodb://localhost:${mongoDBContainer.firstMappedPort}"
        )
    )

    @Bean
    fun reactiveMongoTemplate(mongoClient: MongoClient): ReactiveMongoTemplate =
        ReactiveMongoTemplate(mongoClient, databaseName)

}

