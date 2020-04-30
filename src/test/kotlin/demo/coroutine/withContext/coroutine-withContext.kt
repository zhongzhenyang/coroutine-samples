package demo.coroutine.withContext;

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

val Log = LoggerFactory.getLogger("coroutine-withContext")


suspend fun testWithContext() {
    var resultOne = "Hardstyle"
    var resultTwo = "Minions"
    Log.info("withContext", "Before")
    resultOne = withContext(Dispatchers.IO) { function1() }
    resultTwo = withContext(Dispatchers.IO) { function2() }
    Log.info("withContext", "After")
    val resultText = resultOne + resultTwo
    Log.info("withContext", resultText)
}

suspend fun function1(): String {
    delay(1000L)
    val message = "function1"
    Log.info("withContext", message)
    return message
}

suspend fun function2(): String {
    delay(100L)
    val message = "function2"
    Log.info("withContext", message)
    return message
}

fun main() = runBlocking<Unit> {


//    Log.info("before childrens:${coroutineContext[Job]?.children}")
    testWithContext()
//    Log.info("after childrens:${coroutineContext[Job]?.children}")


}