package net.doemges.kogniswarm.memory

import org.springframework.data.neo4j.repository.Neo4jRepository
import java.util.*

interface MementoRepository : Neo4jRepository<Memento, String>
