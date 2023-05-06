package net.doemges.kogniswarm.http

import jakarta.annotation.PreDestroy
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate

class SeleniumRestTemplate(
    requestFactory: ClientHttpRequestFactory,
    interceptors: List<ClientHttpRequestInterceptor>,
    poolSize: Int,
    private val cache: UrlContentCache
) : RestTemplate(requestFactory) {

    private val domainDelayManager: DomainDelayManager = DomainDelayManager()
    private val containerPool: ContainerPool = ContainerPool(poolSize)

    init {
        this.interceptors.addAll(interceptors)
    }

    override fun <T> execute(
        uriTemplate: String,
        method: HttpMethod,
        requestCallback: RequestCallback?,
        responseExtractor: ResponseExtractor<T>?,
        vararg uriVariables: Any?
    ): T? {
        return executeOnUrl(uriTemplate, responseExtractor)
    }

    @Suppress("SameParameterValue")
    private fun <T> SeleniumRestTemplate.executeOnUrl(
        url: String,
        responseExtractor: ResponseExtractor<T>?
    ): T? {
        cache.get(url)?.let { cachedResponse ->
            logger.info("Loading from cache for URL: $url")
            return responseExtractor?.extractData(SimpleStringClientHttpResponse(cachedResponse))
        }
        val response = getPageWithSelenium(url, responseExtractor)
        cache.put(url, response.toString())
        return response
    }

    override fun <T : Any?> execute(
        uriTemplate: String,
        method: HttpMethod,
        requestCallback: RequestCallback?,
        responseExtractor: ResponseExtractor<T>?,
        uriVariables: MutableMap<String, *>
    ): T? {
        return executeOnUrl(uriTemplate, responseExtractor)
    }

    private fun <T : Any?> getPageWithSelenium(url: String, responseExtractor: ResponseExtractor<T>?): T? {
        val domain = Domain.fromUrl(url)
        domainDelayManager.waitBeforeAccessing(domain)
        val container = containerPool.getContainer()
        val webDriver = container.webDriver
        webDriver.get(url)
        val pageSource = webDriver.pageSource
        val response = SimpleStringClientHttpResponse(pageSource)
        containerPool.returnContainer(container)
        logger.info("Loading from web for URL: $url")
        return responseExtractor?.extractData(response)
    }

    override fun <T : Any?> exchange(
        uriTemplate: String,
        method: HttpMethod,
        requestEntity: HttpEntity<*>?,
        responseType: Class<T>,
        vararg uriVariables: Any?
    ): ResponseEntity<T> {
        val requestCallback = httpEntityCallback<Any>(requestEntity, responseType)
        val responseExtractor = responseEntityExtractor<T>(responseType)
        return execute(uriTemplate, method, requestCallback, responseExtractor, *uriVariables) ?: error("No result")

    }

    @PreDestroy
    fun closeAllWebDrivers() = containerPool.closeAll()

}
