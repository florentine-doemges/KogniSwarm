package net.doemges.kogniswarm.http.model

import net.doemges.kogniswarm.convertStreamToString
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import java.io.ByteArrayInputStream
import java.io.InputStream

class ResponseWrapper(private val inner: ClientHttpResponse) :
    ClientHttpResponse {

    private val body = convertStreamToString(inner.body)
    override fun getHeaders(): HttpHeaders = inner.headers

    override fun getBody(): InputStream = ByteArrayInputStream(body.toByteArray())

    override fun close() = inner.close()

    override fun getStatusCode(): HttpStatusCode = inner.statusCode

    @Suppress("DEPRECATION")
    override fun getRawStatusCode(): Int = inner.rawStatusCode

    override fun getStatusText(): String = inner.statusText

    fun bodyAsString(): String = body
}