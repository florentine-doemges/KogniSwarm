package net.doemges.kogniswarm.memory

import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName

class GenericNeo4JContainer(dockerImageName: DockerImageName) : Neo4jContainer<GenericNeo4JContainer>(dockerImageName){
    constructor(dockerImageName: String): this(DockerImageName.parse(dockerImageName))
}