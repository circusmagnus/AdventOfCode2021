fun<T> List<T>.tail() = this.drop(1)

@OptIn(ExperimentalStdlibApi::class)
operator fun <T> T.plus(other: List<T>): List<T> = buildList<T> {
    add(this@plus)
    addAll(other)
}