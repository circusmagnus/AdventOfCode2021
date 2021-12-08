fun day8(input: List<String>): Int{
    println("just input: $input")
    val outputs = input.justOutputs()

    return outputs.sumOf { output ->
        output
            .map { it.length }
            .count { it == 2 || it == 4 || it == 3 || it == 7 }
    }

}

private fun List<String>.justOutputs(): List<List<String>> = map { entry -> entry.justOutput() }

private fun String.justOutput(): List<String> {
    val output = split("|")[1]
    return output.split(" ")
}