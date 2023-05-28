package net.doemges.kogniswarm.tool.tools.google

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleSearchConfig(
    @Value("\${google.search.custom.api.key}") val googleCustomSearchApiKey: String,
    @Value("\${google.search.custom.engine.id}") val googleCustomSearchEngineId: String
)