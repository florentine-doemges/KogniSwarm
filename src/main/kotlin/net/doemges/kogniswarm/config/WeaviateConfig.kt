package net.doemges.kogniswarm.config

import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.misc.model.InvertedIndexConfig
import io.weaviate.client.v1.misc.model.ReplicationConfig
import io.weaviate.client.v1.misc.model.ShardingConfig
import io.weaviate.client.v1.misc.model.StopwordConfig
import io.weaviate.client.v1.misc.model.VectorIndexConfig
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import net.doemges.kogniswarm.docker.DockerService
import net.doemges.kogniswarm.weaviate.BaseWeaviateClient
import net.doemges.kogniswarm.weaviate.TestableWeaviateClient
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(javaClass)

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
    fun testableWeaviateClient(
        weaviateClient: WeaviateClient
    ): TestableWeaviateClient = BaseWeaviateClient(weaviateClient)

    @Bean
    fun weaviateClient(
        weaviateClientConfig: Config
    ): WeaviateClient = WeaviateClient(weaviateClientConfig)

    @Bean
    fun weaviateClientConfig(weaviateContainer: GenericContainer<*>): Config =
        Config("http", "localhost:${weaviateContainer.getMappedPort(8080)}")


    @Bean
    fun stopwords(
        @Value("\${weaviate.stopwords.language:en}") languagePreset: String = "en"
    ): StopwordConfig =
        StopwordConfig.builder()
            .preset(languagePreset)
            .build()

    @Bean
    fun invertedIndexConfig(
        stopwords: StopwordConfig,
        @Value("\${weaviate.index.timestamps:true}") indexTimestamps: Boolean = true
    ): InvertedIndexConfig = InvertedIndexConfig
        .builder()
        .stopwords(stopwords)
        .indexTimestamps(indexTimestamps)
        .build()

    @Bean
    fun shardingConfig(
        @Value("\${weaviate.sharding.desiredCount:1}") desiredCount: Int = 1,
        @Value("\${weaviate.sharding.actualCount:1}") actualCount: Int = 1,
        @Value("\${weaviate.sharding.function:murmur3}") function: String = "murmur3",
        @Value("\${weaviate.sharding.key:_id}") key: String = "_id",
        @Value("\${weaviate.sharding.strategy:hash}") strategy: String = "hash",
        @Value("\${weaviate.sharding.virtualPerPhysical:128}") virtualPerPhysical: Int = 128,
        @Value("\${weaviate.sharding.desiredVirtualCount:128}") desiredVirtualCount: Int = 128,
        @Value("\${weaviate.sharding.actualVirtualCount:128}") actualVirtualCount: Int = 128
    ): ShardingConfig = ShardingConfig
        .builder()
        .desiredCount(desiredCount)
        .actualCount(actualCount)
        .function(function)
        .key(key)
        .strategy(strategy)
        .virtualPerPhysical(virtualPerPhysical)
        .desiredVirtualCount(desiredVirtualCount)
        .actualVirtualCount(actualVirtualCount)
        .build()

    @Bean
    fun replicationConfig(
        @Value("\${weaviate.replication.factor:1}") factor: Int = 1
    ): ReplicationConfig =
        ReplicationConfig
            .builder()
            .factor(factor)
            .build()

    @Bean
    fun vectorIndexConfig(
        @Value("\${weaviate.vectorIndex.distanceType:cosine}") distanceType: String = "cosine",
        @Value("\${weaviate.vectorIndex.cleanupIntervalSeconds:300}") cleanupIntervalSeconds: Int = 300,
        @Value("\${weaviate.vectorIndex.efConstruction:128}") efConstruction: Int = 128,
        @Value("\${weaviate.vectorIndex.maxConnections:64}") maxConnections: Int = 64,
        @Value("\${weaviate.vectorIndex.vectorCacheMaxObjects:500000}") vectorCacheMaxObjects: Long = 500000L,
        @Value("\${weaviate.vectorIndex.ef:-1}") ef: Int = -1,
        @Value("\${weaviate.vectorIndex.skip:false}") skip: Boolean = false,
        @Value("\${weaviate.vectorIndex.dynamicEfFactor:8}") dynamicEfFactor: Int = 8,
        @Value("\${weaviate.vectorIndex.dynamicEfMax:500}") dynamicEfMax: Int = 500,
        @Value("\${weaviate.vectorIndex.dynamicEfMin:100}") dynamicEfMin: Int = 100,
        @Value("\${weaviate.vectorIndex.flatSearchCutoff:40000}") flatSearchCutoff: Int = 40000
    ): VectorIndexConfig = VectorIndexConfig
        .builder()
        .distance(distanceType)
        .cleanupIntervalSeconds(cleanupIntervalSeconds)
        .efConstruction(efConstruction)
        .maxConnections(maxConnections)
        .vectorCacheMaxObjects(vectorCacheMaxObjects)
        .ef(ef)
        .skip(skip)
        .dynamicEfFactor(dynamicEfFactor)
        .dynamicEfMax(dynamicEfMax)
        .dynamicEfMin(dynamicEfMin)
        .flatSearchCutoff(flatSearchCutoff)
        .build()

    @Bean
    fun memoryClass(
        weaviateClient: WeaviateClient,
        invertedIndexConfig: InvertedIndexConfig,
        shardingConfig: ShardingConfig,
        replicationConfig: ReplicationConfig,
        vectorIndexConfig: VectorIndexConfig
    ): WeaviateClass {
        val out = replicationConfig(
            className = "Memory",
            invertedIndexConfig = invertedIndexConfig,
            shardingConfig = shardingConfig,
            vectorIndexConfig = vectorIndexConfig,
            replicationConfig = replicationConfig
        )
            .properties(
                listOf(
                    Property.builder()
                        .name("description")
                        .description("description of the action")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("result")
                        .description("result of the action")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("userName")
                        .description("the user who ordered the action")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("agentName")
                        .description("the agent who executed the action")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("userPrompt")
                        .description("the prompt that was given by the user")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("toolName")
                        .description("the name of the tool that was used")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("toolDescription")
                        .description("description of the tool that was used")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("args")
                        .description("arguments that were given to the tool")
                        .dataType(listOf(DataType.TEXT))
                        .build(),
                    Property.builder()
                        .name("timestamp")
                        .description("timestamp of the action")
                        .dataType(listOf(DataType.INT))
                        .build(),
                )
            )
            .build()
            .also { weaviateClass ->
                val schemaResult = weaviateClient.schema()
                    .classGetter()
                    .withClassName("Memory")
                    .run()
                if (schemaResult.hasErrors()) {
                    for (message in schemaResult.error.messages) {
                        logger.error(message.message)
                    }
                    return@also
                }
                schemaResult.result?.also {
                    val classDeleteResult = weaviateClient.schema()
                        .classDeleter()
                        .withClassName("Memory")
                        .run()
                    if (classDeleteResult.hasErrors()) {
                        for (message in classDeleteResult.error.messages) {
                            logger.error(message.message)
                        }
                    }
                }
                val result = weaviateClient.schema()
                    .classCreator()
                    .withClass(weaviateClass)
                    .run()
                if (result.hasErrors()) {
                    for (message in result.error.messages) {
                        logger.error(message.message)
                    }
                }

            }
        return out
    }

    private fun replicationConfig(
        className: String,
        invertedIndexConfig: InvertedIndexConfig,
        shardingConfig: ShardingConfig,
        vectorIndexConfig: VectorIndexConfig,
        replicationConfig: ReplicationConfig,
        vectorizer: String = "text2vec-openai"
    ): WeaviateClass.WeaviateClassBuilder = WeaviateClass
        .builder()
        .className(className)
        .vectorizer(vectorizer)
        .invertedIndexConfig(invertedIndexConfig)
        .shardingConfig(shardingConfig)
        .vectorIndexConfig(vectorIndexConfig)
        .replicationConfig(replicationConfig)

}

