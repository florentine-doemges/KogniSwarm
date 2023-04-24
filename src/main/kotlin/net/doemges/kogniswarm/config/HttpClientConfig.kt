package net.doemges.kogniswarm.config

import net.doemges.kogniswarm.http.ResponseWrapper
import net.doemges.kogniswarm.http.SeleniumRestTemplate
import net.doemges.kogniswarm.http.UrlContentCache
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.io.InputStream
import java.util.zip.GZIPInputStream

@Configuration
class HttpClientConfig {

    private val logger = LoggerFactory.getLogger(HttpClientConfig::class.java)

    @Suppress("DEPRECATION", "removal")
    private val loggingInterceptor =
        ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
            logger.debug(
                "----------------\n{} {}\n{}\n----------------\n{}\n----------------",
                request.methodValue,
                request.uri,
                request.headers,
                body.decodeToString()
            )

            val response = ResponseWrapper(execution.execute(request, body))
            logger.debug(
                "{}\n{}\n----------------\n{}\n----------------\n",
                response.statusCode,
                response.headers,
                response.bodyAsString()
            )

            response
        }

    @Bean
    fun restTemplate(cache: UrlContentCache): RestTemplate {
        val requestFactory = SimpleClientHttpRequestFactory().apply {
            setBufferRequestBody(true)
        }
        val interceptors = listOf(loggingInterceptor, GzipResponseInterceptor())

        return SeleniumRestTemplate(requestFactory, interceptors, 5, cache)
    }
}

class GzipResponseInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        request.headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip")
        val response = execution.execute(request, body)

        val contentEncoding = response.headers.getFirst(HttpHeaders.CONTENT_ENCODING)
        if (contentEncoding != null && contentEncoding.equals("gzip", ignoreCase = true)) {
            return GzipClientHttpResponse(response)
        }

        return response
    }
}

class GzipClientHttpResponse(private val response: ClientHttpResponse) : ClientHttpResponse by response {
    override fun getBody(): InputStream {
        return GZIPInputStream(response.body)
    }
}
