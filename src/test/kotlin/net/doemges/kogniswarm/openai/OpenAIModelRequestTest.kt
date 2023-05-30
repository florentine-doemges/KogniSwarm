package net.doemges.kogniswarm.openai

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.model.ModelPermission
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OpenAIModelRequestTest {

    private lateinit var model: Model
    private lateinit var modelPermission: ModelPermission

    @BeforeEach
    fun setUp() {
        modelPermission = mockk()
        model = mockk {
            every { id } returns ModelId("gpt-4")
            every { permission } returns listOf(modelPermission)
        }
    }

    @Test
    fun `test matches with correct parameters`() {
        testModelRequest(true)
    }

    @Test
    fun `test matches with incorrect parameters`() {
        testModelRequest(false)
    }

    private fun testModelRequest(value: Boolean) {
        stubModelPermission(value)
        val openAIModelRequest = OpenAIModelRequest.builder {
            modelName("gpt-4")
            isGPT4allowed(value)
            allowCreateEngine(value)
            allowSampling(value)
            allowLogprobs(value)
            allowSearchIndices(value)
            allowView(value)
            allowFineTuning(value)
            isBlocking(value)
        }
            .build()

        val result = openAIModelRequest.matches(model)

        assertThat(result).isEqualTo(value)

    }

    private fun stubModelPermission(value: Boolean) {
        every { modelPermission.allowCreateEngine } returns value
        every { modelPermission.allowSampling } returns value
        every { modelPermission.allowLogprobs } returns value
        every { modelPermission.allowSearchIndices } returns value
        every { modelPermission.allowView } returns value
        every { modelPermission.allowFineTuning } returns value
        every { modelPermission.isBlocking } returns value
    }
}
