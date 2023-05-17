package net.doemges.kogniswarm.core

interface ComponentBuilder<X : Component> {
    fun build(): X
}