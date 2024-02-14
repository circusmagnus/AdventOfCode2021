package year2023

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import java.math.BigInteger

/**
 * Instrukcje w sekwencji
 *
 * Node, ktory ma dwojke dzieci
 * Zbudujemy graf i bedziemy sobie po nim chodzic
 */

fun day8(input: List<String>): String {
    val instructions = input.first()
    val nodes = input.drop(2).toNodes()
//    println("instructions: ${instructions.take(6).toList()}, nodes: $nodes")
    return traverseMany(instructions.toList(), nodes).toString()
}

private fun traverseMany(directions: List<Char>, nodes: Map<String, Node>): BigInteger {
    val startingNodes = nodes.keys.filter { it.last() == 'A' }.map { key -> nodes[key]!! }

    val results = startingNodes.map { startingNode ->
        traverse(directions, nodes, startingNode)
    }.onEach { println("visits: $it") }

    return results.fold(BigInteger.ONE) { acc, value -> acc * value.toBigInteger() }
}

private fun traverse(directions: List<Char>, nodes: Map<String, Node>, startingNode: Node): Long {

    tailrec fun go(dirLeft: List<Char>, currentNode: Node, visitCount: Long): Long {
        if (currentNode.name.last() == 'Z') return visitCount

        val goFromStart = dirLeft.isEmpty()
        val currDir = if (goFromStart) directions.first() else dirLeft.first()
        val newNode = if(currDir == 'L')  nodes[currentNode.left] else nodes[currentNode.right]
        val newDirLeft = if (goFromStart) directions.drop(1) else dirLeft.drop(1)

        return go(dirLeft = newDirLeft, currentNode = newNode!!, visitCount + 1)
    }

    return go(directions, startingNode, 0L)
}

private fun List<String>.toNodes() : Map<String, Node> {
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