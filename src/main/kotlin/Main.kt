import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.produceIn
import year2022.day10

@ExperimentalStdlibApi
fun main() {

    println("answer: ${day10(getData("src/main/resources/year2022/Day10"))}")


}

//fun <T> Flow<T>.chunked(intervalMs: Long, maxSize: Int): Flow<List<T>> = flow {
//    coroutineScope {
//        val buffer = produceToChannel(capacity = maxSize) { channel ->
//            collect { elem -> channel.send(elem) }
//        }
//
//        while (true) {
//            delay(intervalMs)
//            val firstElem = buffer.receiveCatching().getOrElse { error ->
//                if(error == null) null else throw error
//            } ?: break
//            val chunk = buffer.drain(mutableListOf(firstElem), maxSize)
//            emit(chunk)
//        }
//    }
//}
//
//fun <T> CoroutineScope.produceToChannel(
//    capacity: Int,
//    feedChannel: suspend CoroutineScope.(SendChannel<T>) -> Unit
//): Producer<T> {
//    val channel = Channel<T>(capacity)
//    val job = launch {
//        try {
//            feedChannel(channel)
//        } catch (t: Throwable) {
//            channel.close(t)
//        } finally {
//            channel.close()
//        }
//    }
//    return Producer(channel, job)
//}
//
//class Producer<T> (channel: ReceiveChannel<T>, val job: Job): ReceiveChannel<T> by channel
//
//private tailrec fun <T> ReceiveChannel<T>.drain(acc: MutableList<T> = mutableListOf(), maxElements: Int): List<T> =
//    if (acc.size == maxElements) acc
//    else {
//        val nextValue = tryReceive().getOrElse { error: Throwable? -> error?.let { throw(it) } ?: return acc }
//        acc.add(nextValue)
//        drain(acc, maxElements)
//    }