package net.doemges.kogniswarm.http.model

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import java.io.ByteArrayInputStream
import java.io.InputStream

class SimpleStringClientHttpResponse(body: String) : ClientHttpResponse {
    private val inputStream =
        ByteArrayInputStream(body.toByteArray())

    override fun getStatusCode(): HttpStatus = HttpStatus.OK

    @Deprecated("Deprecated in Java", ReplaceWith("HttpStatus.OK.value()", "org.springframework.http.HttpStatus"))
    override fun getRawStatusCode(): Int = HttpStatus.OK.value()

    override fun getStatusText(): String = HttpStatus.OK.reasonPhrase

    override fun close() = inputStream.close()

    override fun getBody(): InputStream = inputStream

    override fun getHeaders(): HttpHeaders = HttpHeaders.EMPTY
}