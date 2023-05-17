package net.doemges.kogniswarm.prompt

import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import net.doemges.kogniswarm.command.Command
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Prompt(
    val constraints: List<Constraint>,
    val resources: List<Resource>,
    val performanceEvaluations: List<PerformanceEvaluation>,
    val commands: List<Command>,
    val userRequest: String,
    val name: String? = null,
    val mission: String? = null
) {
    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        fun builder(commands: List<Command>, standards: Boolean = true, block: Builder.() -> Unit = {}) =
            Builder(block).apply {
                if (standards) {
                    constraints(
                        *Constraints.standardConstraints()
                            .toTypedArray()
                    )
                    resources(
                        *Resources.standardResources()
                            .toTypedArray()
                    )
                    performanceEvaluations(
                        *PerformanceEvaluations.standardPerformanceEvaluations()
                            .toTypedArray()
                    )
                    commands(*commands.toTypedArray())
                }
            }

        fun fromUserRequest(
            userRequest: String,
            commands: List<Command> = emptyList(),
            standards: Boolean = true
        ) =
            builder(commands, standards) {
                userRequest(userRequest)
            }.build()
    }

    fun toChatBundle(): ChatMessageBundle {
        val input = userRequest

        val sections = listOf(
            "Constraints" to constraints,
            "Resources" to resources,
            "Commands" to commands,
            "Performance Evaluations" to performanceEvaluations
        )

        val formattedSections = sections.mapNotNull { (title, list) ->
            list.takeIf { it.isNotEmpty() }
                ?.joinToString(prefix = "$title:\n", separator = "\n") { "- $it" }
        }

        val currentTime = "The current time and date is ${
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }\n"

        val name: String? = name?.let { "Your name is $name\n" }

        val mission: String? = mission?.let { "Your mission is $mission\n" }

        val systemInput = (listOf(name) + mission + formattedSections + currentTime)
            .filterNotNull()
            .joinToString("\n")

        return ChatMessageBundle.fromInput(input, systemInput)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(block: Builder.() -> Unit = {}) {
        private val constraints: MutableList<Constraint> = mutableListOf()
        private val resources: MutableList<Resource> = mutableListOf()
        private val performanceEvaluations: MutableList<PerformanceEvaluation> = mutableListOf()
        private val commands: MutableList<Command> = mutableListOf()
        private var userRequest: String? = null
        private var name: String? = null
        private var mission: String? = null

        init {
            block()
        }

        fun build() = Prompt(
            constraints,
            resources,
            performanceEvaluations,
            commands,
            userRequest ?: error("userRequest must be set"),
            name,
            mission
        )

        fun constraint(constraint: Constraint) = apply { constraints.add(constraint) }

        fun constraints(vararg constraints: Constraint) = apply { this.constraints.addAll(constraints) }

        fun resource(resource: Resource) = apply { resources.add(resource) }

        fun resources(vararg resources: Resource) = apply { this.resources.addAll(resources) }

        fun userRequest(userRequest: String) = apply { this.userRequest = userRequest }

        fun performanceEvaluation(performanceEvaluation: PerformanceEvaluation) =
            apply { performanceEvaluations.add(performanceEvaluation) }

        fun performanceEvaluations(vararg performanceEvaluations: PerformanceEvaluation) =
            apply { this.performanceEvaluations.addAll(performanceEvaluations) }

        fun command(command: Command) = apply { commands.add(command) }

        fun commands(vararg commands: Command) = apply { this.commands.addAll(commands) }
    }
}



