package net.doemges.kogniswarm.prompt

data class Resource(val text: String){
    override fun toString(): String = text
}

@Suppress("MemberVisibilityCanBePrivate")
class Resources private constructor() {
    companion object {
        fun internetSearch() = Resource("Internet access for searches and information gathering.")

        fun longTermMemoryManagement() = Resource("Long term memory management")


        fun gptAgents() = Resource("GPT-3.5 powered Agents for delegation of simple tasks.")


        fun fileOutput() = Resource("File output.")

        fun standardResources(): List<Resource> =
            listOf(internetSearch(), longTermMemoryManagement(), gptAgents(), fileOutput())
    }
}