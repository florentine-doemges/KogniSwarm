package net.doemges.kogniswarm.core.util

import org.apache.camel.AggregationStrategy
import org.apache.camel.Exchange

class CustomAggregationStrategy : AggregationStrategy {
    override fun aggregate(oldExchange: Exchange?, newExchange: Exchange?): Exchange {
        if (oldExchange == null) {
            return newExchange!!
        }

        if (newExchange == null) {
            return oldExchange
        }

        val oldHeaders = oldExchange.getIn().headers
        val newHeaders = newExchange.getIn().headers

        oldHeaders.putAll(newHeaders)

        return oldExchange
    }
}