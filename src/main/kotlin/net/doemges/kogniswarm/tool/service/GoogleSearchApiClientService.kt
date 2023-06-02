package net.doemges.kogniswarm.tool.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.v1.Customsearch
import com.google.api.services.customsearch.v1.CustomsearchRequestInitializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import net.doemges.kogniswarm.tool.model.Item
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GoogleSearchApiClientService(
    @Value("\${google.search.custom.api.key}") private val googleCustomSearchApiKey: String,
    @Value("\${google.search.custom.engine.id}") private val googleCustomSearchEngineId: String
) {

    private val cs: Customsearch by lazy {
        Customsearch.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null)
            .setApplicationName("MyApplication")
            .setGoogleClientRequestInitializer(CustomsearchRequestInitializer(googleCustomSearchApiKey))
            .build()
    }

    fun fetchItems(query: String, start: Int, num: Int): Flow<Item> = channelFlow {
        val list = cs.cse()
            .list()
            .setCx(googleCustomSearchEngineId)
            .setStart(start.toLong())
            .setNum(num)
            .setQ(query)
        val result = list.execute()
        result.items?.forEach { send(Item(it.title, it.link)) }
    }
}
