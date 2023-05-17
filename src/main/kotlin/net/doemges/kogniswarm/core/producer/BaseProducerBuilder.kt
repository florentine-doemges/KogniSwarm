package net.doemges.kogniswarm.core.producer

import net.doemges.kogniswarm.core.BaseComponentBuilder

open class BaseProducerBuilder<T : BaseProducer> : ProducerBuilder<T>, BaseComponentBuilder<T>() {
    @Suppress("UNCHECKED_CAST")
    override fun build(): T = BaseProducer() as T

}
