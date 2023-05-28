package net.doemges.kogniswarm.openai

import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelPermission

data class OpenAIModelRequest(
    val allowCreateEngine: Boolean? = null,
    val allowSampling: Boolean? = null,
    val allowLogprobs: Boolean? = null,
    val allowSearchIndices: Boolean? = null,
    val allowView: Boolean? = null,
    val allowFineTuning: Boolean? = null,
    val isBlocking: Boolean? = null,
    val isGPT4allowed: Boolean? = null,
    val modelName: String? = null
) {
    fun matches(model: Model): Boolean {
        if (modelName == null || !model.id.id.contains(modelName, ignoreCase = true))
            return false
        if (isGPT4allowed == null || isGPT4allowed != model.id.id.contains("gpt-4"))
            return false
        val anyPermissionMatches = model.permission.any { permission ->
            matches(permission)
        }
        return anyPermissionMatches
    }

    private fun matches(permission: ModelPermission): Boolean {
        if (allowCreateEngine != null && allowCreateEngine != permission.allowCreateEngine) return false
        if (allowSampling != null && allowSampling != permission.allowSampling) return false
        if (allowLogprobs != null && allowLogprobs != permission.allowLogprobs) return false
        if (allowSearchIndices != null && allowSearchIndices != permission.allowSearchIndices) return false
        if (allowView != null && allowView != permission.allowView) return false
        if (allowFineTuning != null && allowFineTuning != permission.allowFineTuning) return false
        return !(isBlocking != null && isBlocking != permission.isBlocking)
    }



    companion object {
        fun builder(block: Builder.() -> Unit) = Builder().apply(block)
    }

    class Builder {
        private var allowCreateEngine: Boolean? = null
        private var allowSampling: Boolean? = null
        private var allowLogprobs: Boolean? = null
        private var allowSearchIndices: Boolean? = null
        private var allowView: Boolean? = null
        private var allowFineTuning: Boolean? = null
        private var isBlocking: Boolean? = null
        private var isGPT4allowed: Boolean? = null
        private var modelName: String? = null

        fun modelName(modelName: String) = apply { this.modelName = modelName }

        fun isGPT4allowed(isGPT4allowed: Boolean = true) = apply { this.isGPT4allowed = isGPT4allowed }

        fun allowCreateEngine(allowCreateEngine: Boolean = true) = apply { this.allowCreateEngine = allowCreateEngine }
        fun allowSampling(allowSampling: Boolean = true) = apply { this.allowSampling = allowSampling }

        fun allowLogprobs(allowLogprobs: Boolean = true) = apply { this.allowLogprobs = allowLogprobs }
        fun allowSearchIndices(allowSearchIndices: Boolean = true) =
            apply { this.allowSearchIndices = allowSearchIndices }

        fun allowView(allowView: Boolean = true) = apply { this.allowView = allowView }
        fun allowFineTuning(allowFineTuning: Boolean = true) = apply { this.allowFineTuning = allowFineTuning }
        fun isBlocking(isBlocking: Boolean = true) = apply { this.isBlocking = isBlocking }


        fun build() = OpenAIModelRequest(
            allowCreateEngine,
            allowSampling,
            allowLogprobs,
            allowSearchIndices,
            allowView,
            allowFineTuning,
            isBlocking,
            isGPT4allowed,
            modelName
        )
    }
}