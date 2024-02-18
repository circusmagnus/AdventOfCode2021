package year2023

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import leastCommonMultiple
import java.math.BigInteger

/**
 * Instrukcje w sekwencji
 *
 * Node, ktory ma dwojke dzieci
 * Zbudujemy graf i bedziemy sobie po nim chodzic
 */

fun day8(input: List<String>): Long {
    val instructions = input.first()
    val nodes = input.drop(2).toNodes()
    val startingNodeKeys = nodes.keys.filter { it.last() == 'A' }
//    println("instructions: ${instructions.take(6).toList()}, nodes: $nodes")
//    return traverseMany2(directions = instructions.toList(), nodes = nodes, startingNodeKeys = startingNodeKeys)
    return traverseMany(directions = instructions.toList(), nodes = nodes)
}

private fun nodeTraversalMap(nodes: Map<String, Node>, directions: List<Char>): Map<String, String> {

    tailrec fun go(directionsIndex: Int, map: MutableMap<String, String>, parent: String): Map<String, String> {
        if (directionsIndex == directions.size) return map

        val dir = directions[directionsIndex]
        val child = if (dir == 'L') nodes[parent]!!.left else nodes[parent]!!.right
        map[parent] = child
        return go(directionsIndex + 1, map = map, parent = child)
    }
    return go(0, mutableMapOf(), "A")
}

private fun traverseMany(directions: List<Char>, nodes: Map<String, Node>): Long {
    val startingNodes = nodes.keys.filter { it.last() == 'A' }.map { key -> nodes[key]!! }

    val results = startingNodes.map { startingNode ->
        traverse(directions, nodes, startingNode)
    }.onEach { println("visits: $it") }

    return leastCommonMultiple(results.map { it.toLong() })
}

private fun traverseMany2(directions: List<Char>, nodes: Map<String, Node>, startingNodeKeys: List<String>): Long {

    tailrec fun go(directionsIndex: Int, currentNodesKeys: List<String>, visitCount: Long): Long {
        if (currentNodesKeys.all { it.last() == 'Z' }) return visitCount
        val currDir = directions[directionsIndex]
        val newDirIndex = if (directionsIndex == directions.lastIndex) 0 else directionsIndex + 1
        val newKeys = currentNodesKeys.map { key ->
            val node = nodes[key]!!
            if (currDir == 'L') node.left else node.right
        }

        return go(newDirIndex, newKeys, visitCount + 1)
    }

    return go(directionsIndex = 0, currentNodesKeys = startingNodeKeys, visitCount = 0)
}

private fun traverse(directions: List<Char>, nodes: Map<String, Node>, startingNode: Node): Int {

    tailrec fun go(dirLeft: List<Char>, currentNode: Node, visitCount: Int): Int {
        if (currentNode.name.last() == 'Z') return visitCount

        val goFromStart = dirLeft.isEmpty()
        val currDir = if (goFromStart) directions.first() else dirLeft.first()
        val newNode = if (currDir == 'L') nodes[currentNode.left] else nodes[currentNode.right]
        val newDirLeft = if (goFromStart) directions.drop(1) else dirLeft.drop(1)

        return go(dirLeft = newDirLeft, currentNode = newNode!!, visitCount + 1)
    }

    return go(directions, startingNode, 0)
}

private fun List<String>.toNodes(): Map<String, Node> {
    val listed = map { it.toNode() }

    return buildMap {
        listed.forEach { node -> put(node.name, node) }
    }
}

private fun String.toNode(): Node {
    val byEquals = split(" = ")
    val name = byEquals.first()
    val children = byEquals.last().split(", ")
    val left = children.first().trim().filterNot { it == '(' }
    val right = children.last().trim().filterNot { it == ')' }

    return Node(name = name, left = left, right = right)
}

data class Node(val name: String, val left: String, val right: String)