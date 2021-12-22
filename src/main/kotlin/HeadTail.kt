fun<T> List<T>.tail() = this.drop(1)

@OptIn(ExperimentalStdlibApi::class)
operator fun <T> T.plus(other: List<T>): List<T> = buildList<T> {
    add(this@plus)
    addAll(other)
}

fun<K> MutableMap<K, Long>.putOrIncrement(key: K, value: Long) {
    val entry = this[key]
    val initialValue = entry ?: 0L
    val newValue = initialValue + value
    this[key] = newValue
}