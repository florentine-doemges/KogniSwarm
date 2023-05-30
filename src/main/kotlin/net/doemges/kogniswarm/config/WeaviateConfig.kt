package net.doemges.kogniswarm.config

import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import net.doemges.kogniswarm.docker.DockerService
import net.doemges.kogniswarm.weaviate.BaseWeaviateClient
import net.doemges.kogniswarm.weaviate.TestableWeaviateClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

@Configuration
@DependsOn("dockerService")
@Lazy
class WeaviateConfig(@Suppress("UNUSED_PARAMETER") dockerService: DockerService) {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun weaviateContainer(
        @Value("\${openai.api.key}") openAIApiKey: String
    ): GenericContainer<*> {

        return GenericContainer<Nothing>("semitechnologies/weaviate:1.19.5").apply {
            setWaitStrategy(Wait.forHttp("/v1/.well-known/ready"))
            withExposedPorts(8080)
            envMap["QUERY_DEFAULTS_LIMIT"] = "20"
            envMap["AUTHENTICATION_ANONYMOUS_ACCESS_ENABLED"] = "true"
            envMap["PERSISTENCE_DATA_PATH"] = "/data"
            envMap["DEFAULT_VECTORIZER_MODULE"] = "text2vec-openai"
            envMap["ENABLE_MODULES"] = "text2vec-openai"
            envMap["OPENAI_APIKEY"] = openAIApiKey
            envMap["CLUSTER_HOSTNAME"] = "node1"
            withFileSystemBind(
                "src/main/resources/data/weaviate",
                "/data"
            ) // Mount the ./data directory on the host into the /data directory in the container

        }
    }


    @Bean
    fun weaviateClient(
        weaviateClientConfig: Config
    ): TestableWeaviateClient = BaseWeaviateClient(WeaviateClient(weaviateClientConfig))

    @Bean
    fun weaviateClientConfig(weaviateContainer: GenericContainer<*>): Config =
        Config("http", "localhost:${weaviateContainer.getMappedPort(8080)}")


}

