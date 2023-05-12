package net.doemges.kogniswarm.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DependencySorterTest {

    @Test
    fun testSortWithDependency() {
        val items = listOf("Shirt", "Underwear", "Pants", "Shoes", "Socks", "Belt", "Watch")
        val sortedItems = DependencySorter.sortWithDependency(items) { item, dependsOn ->
            when {
                item == "Shirt" && dependsOn == "Underwear" -> true
                item == "Pants" && (dependsOn == "Underwear" || dependsOn == "Shirt") -> true
                item == "Belt" && dependsOn == "Pants" -> true
                item == "Shoes" && (dependsOn == "Pants" || dependsOn == "Socks") -> true
                item == "Socks" && dependsOn == "Pants" -> true
                else -> false
            }
        }

        assertEquals(listOf("Underwear", "Shirt", "Pants", "Socks", "Shoes", "Belt", "Watch"), sortedItems)
    }

}
