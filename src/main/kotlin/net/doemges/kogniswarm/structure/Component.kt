package net.doemges.kogniswarm.structure

open class Component<out X : Component<X>>(val id: String)
open class ComponentBuilder<out X : Component<X>>(protected val id: String) {
    open fun build(): Component<X> = Component(id)
}

fun <T : Component<T>, X : ComponentBuilder<T>> createComponent(
    builder: X,
    @Suppress("unused") block: X.() -> Unit = {}
): Component<T> =
    builder.run {
        block()
        build()
    }