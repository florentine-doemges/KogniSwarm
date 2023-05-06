package net.doemges.kogniswarm.summary

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.appmattus.kotlinfixture.kotlinFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate

class TikTokenTest {

    private val restTemplate = mockk<RestTemplate>()

    private val tikToken = TikToken(restTemplate)

    private val fixture = kotlinFixture()

    @Test
    fun `test encodingForModel for known models`() {
        val modelsAndEncodings = mapOf(
            "gpt-4" to "cl100k_base",
            "gpt-3.5-turbo" to "cl100k_base",
            "gpt-4-custom" to "cl100k_base",
            "gpt-3.5-turbo-custom" to "cl100k_base"
        )

        every { restTemplate.getForObject("https://openaipublic.blob.core.windows.net/encodings/cl100k_base.tiktoken", String::class.java) } returns "dummyResponse"

        modelsAndEncodings.forEach { (modelName, expectedEncoding) ->
            val encoding = tikToken.encodingForModel(modelName)
            assertThat(encoding.name).isEqualTo(expectedEncoding)
        }
    }

    @Test
    fun `test encodingForModel for unknown model`() {
        assertThat { tikToken.encodingForModel("unknown-model") }.isFailure()
                .isInstanceOf(IllegalArgumentException::class)
    }

    @Test
    fun `test cl100kBase`() {
        val dummyResponse = fixture<String>()
        every { restTemplate.getForObject(any<String>(), String::class.java) } returns dummyResponse

        val encoding = tikToken.cl100kBase()

        assertThat(encoding.name).isEqualTo("cl100k_base")
        assertThat(encoding.mergeableRanks).isEqualTo(dummyResponse)

        val specialTokens = encoding.specialTokens
        assertThat(specialTokens).contains("ENDOFTEXT", 100257)
        assertThat(specialTokens).contains("FIM_PREFIX", 100258)
        assertThat(specialTokens).contains("FIM_MIDDLE", 100259)
        assertThat(specialTokens).contains("FIM_SUFFIX", 100260)
        assertThat(specialTokens).contains("ENDOFPROMPT", 100276)
    }

    @Test
    fun `test downloadFile`() {
        val url = "https://test-url"
        val expectedResponse = fixture<String>()
        every { restTemplate.getForObject(url, String::class.java) } returns expectedResponse

        val actualResponse = tikToken.downloadFile(url)

        assertThat(actualResponse).isEqualTo(expectedResponse)
    }
}
