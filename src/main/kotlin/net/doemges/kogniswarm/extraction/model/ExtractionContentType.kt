package net.doemges.kogniswarm.extraction.model

import java.util.Locale

enum class ExtractionContentType {
    TEXT,
    IMAGES,
    LINKS;

    override fun toString(): String = super
        .toString()
        .lowercase(Locale.getDefault())

    companion object {
        fun ofString(string: String): ExtractionContentType? = values().find { it.toString() == string }
    }
}