package net.doemges.kogniswarm.core.util

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class Builder<T : Any>(private val kClass: KClass<T>) {
    private val values = mutableMapOf<KParameter, Any?>()

    fun <V> set(property: KProperty1<T, V>, value: V): Builder<T> {
        val parameter = kClass.primaryConstructor!!.parameters.first { it.name == property.name }
        values[parameter] = value
        return this
    }

    fun <V : Any, X : Builder<V>> set(property: KProperty1<T, V>, builder: X): Builder<T> =
        set(property, builder.build())

    fun build(): T = kClass.primaryConstructor!!.callBy(values)
}

fun <T : Any> createBuilder(kClass: KClass<T>, block: Builder<T>.() -> Unit = {}): Builder<T> =
    Builder(kClass).apply(block)

fun <T : Any> KClass<T>.builder(block: Builder<T>.() -> Unit = {}): Builder<T> = createBuilder(this, block)

inline fun <reified T : Any> T.copyBuilder(): Builder<T> {
    val kClass = T::class
    val builder = Builder(kClass)
    val primaryConstructor = kClass.primaryConstructor!!

    kClass.memberProperties.forEach { property ->
        val parameter = primaryConstructor.parameters.firstOrNull { it.name == property.name }

        if (parameter != null) {
            val value = property.get(this)

            if (value != null) {
                if (value is Builder<*>) {
                    builder.set(property, value)
                } else {
                    builder.set(property, value)
                }
            }
        }
    }

    return builder
}

inline fun <reified T : Any> T.copy(block: Builder<T>.() -> Unit = {}): T = copyBuilder()
    .apply(block)
    .build()


// Usage
fun main() {
    data class MyClass(val name: String, val age: Int)
    data class YourClass(val you: MyClass, val me: MyClass)

    val myClassBuilder = MyClass::class.builder {
        set(MyClass::name, "John")
        set(MyClass::age, 25)
    }
    val myClassInstance = myClassBuilder.build()

    val yourClassBuilder: Builder<YourClass> = YourClass::class.builder {
        set(YourClass::me, MyClass::class.builder {
            set(MyClass::name, "John")
            set(MyClass::age, 25)
        })
        set(YourClass::you, MyClass::class.builder {
            set(MyClass::name, "Mary")
            set(MyClass::age, 21)
        })

    }

    println(myClassInstance)
    println(yourClassBuilder.build())
}
