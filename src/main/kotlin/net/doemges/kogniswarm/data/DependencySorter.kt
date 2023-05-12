package net.doemges.kogniswarm.data

class DependencySorter private constructor() {
    companion object {
        fun <T> sortWithDependency(list: List<T>, dependsOn: (T, T) -> Boolean): List<T> {
            val result = mutableListOf<T>()
            val visited = mutableSetOf<T>()
            val stack = mutableListOf<T>()

            for (item in list) {
                if (item !in visited) {
                    visit(item, list, dependsOn, visited, stack)
                }
            }
            while (stack.isNotEmpty()) {
                result.add(stack.removeAt(stack.lastIndex))
            }
            return result.reversed()
        }

        private fun <T> visit(
            node: T, list: List<T>, dependsOn: (T, T) -> Boolean,
            visited: MutableSet<T>, stack: MutableList<T>
        ) {
            visited.add(node)
            for (neighbor in list) {
                if (dependsOn(node, neighbor) && neighbor !in visited) {
                    visit(neighbor, list, dependsOn, visited, stack)
                }
            }
            stack.add(node)
        }
    }
}
