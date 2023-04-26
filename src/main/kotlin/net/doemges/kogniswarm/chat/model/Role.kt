package net.doemges.kogniswarm.chat.model

import com.fasterxml.jackson.annotation.JsonValue

enum class Role(@JsonValue private val role: String) {
    USER("user"), SYSTEM("system"), ASSISTANT("assistant");

    override fun toString() = role


}
