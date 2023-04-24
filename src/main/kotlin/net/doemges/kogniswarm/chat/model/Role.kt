package net.doemges.kogniswarm.chat.model

enum class Role(private val role: String) {
    USER("user"), SYSTEM("system"), ASSISTANT("assistant");

    override fun toString() = role

}