package net.doemges.kogniswarm.extraction

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.extraction.util.ContentExtractor
import net.doemges.kogniswarm.extraction.model.Extract
import net.doemges.kogniswarm.extraction.model.ExtractionContentType
import org.apache.tika.Tika
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.io.InputStream

class ContentExtractorTest {
    private val mockTika: Tika = mockk()




    private fun doTest(
        mimeType: String,
        contentType: ExtractionContentType,
        mockContent: String,
        expectedExtract: List<Extract>
    ) {
        every { mockTika.detect(any<InputStream>()) } returns mimeType

        val contentExtractor = ContentExtractor(contentType, mockTika)

        runBlocking {
            val result = contentExtractor.extract(Mono.just(mockContent))
                .toList()

            assertThat(result).isEqualTo(expectedExtract)
        }

        verify { mockTika.detect(any<InputStream>()) }
    }

    @Test
    fun `extract should return a flow of Extracts`() {

        doTest(
            mimeType = "text/plain",
            contentType = ExtractionContentType.TEXT,
            mockContent = "test content",
            expectedExtract = listOf(Extract("test content"))
        )
    }

    @Test
    fun `extract should handle HTML content correctly`() {

        doTest(
            mimeType = "text/html",
            contentType = ExtractionContentType.TEXT,
            mockContent = "<html><body><p>test content</p></body></html>",
            expectedExtract = listOf(Extract("test content"))
        )

    }

    @Test
    fun `extract should handle non-text content correctly`() {

        every { mockTika.parseToString(any<InputStream>()) } returns "non-text content"

        doTest(
            mimeType = "application/octet-stream",
            contentType = ExtractionContentType.TEXT,
            mockContent = "non-text content",
            expectedExtract = listOf(Extract("non-text content"))
        )

        verify { mockTika.parseToString(any<InputStream>()) }
    }

    @Test
    fun `extract should handle URLs correctly`() {
        doTest(
            mimeType = "text/plain",
            contentType = ExtractionContentType.LINKS,
            mockContent = "Check this out: https://example.com",
            expectedExtract = listOf(Extract("https://example.com"))
        )

    }

    @Test
    fun `extract should handle multiple links correctly`() {

        doTest(
            mimeType = "text/plain",
            contentType = ExtractionContentType.LINKS,
            mockContent = "Check these out: https://example1.com and https://example2.com",
            expectedExtract = listOf(Extract("https://example1.com"), Extract("https://example2.com"))
        )

    }

    @Test
    fun `extract should handle multiple images correctly`() {

        doTest(
            mimeType = "text/html",
            contentType = ExtractionContentType.IMAGES,
            mockContent = """
                        <html>
                            <body>
                                <img src="https://example1.com/image1.jpg"/>
                                <img src="https://example2.com/image2.jpg"/>
                            </body>
                        </html>
                    """,
            expectedExtract = listOf(
                Extract("https://example1.com/image1.jpg"),
                Extract("https://example2.com/image2.jpg")
            )
        )

    }


}
