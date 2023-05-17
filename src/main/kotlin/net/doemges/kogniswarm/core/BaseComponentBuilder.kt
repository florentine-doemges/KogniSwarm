package net.doemges.kogniswarm.core

abstract class BaseComponentBuilder<T : Component> : ComponentBuilder<T> {
    abstract override fun build(): T
}