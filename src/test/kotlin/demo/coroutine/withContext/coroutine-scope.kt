package demo.coroutine.useScope

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

val Log = LoggerFactory.getLogger("coroutine-useScope")


suspend fun loadAndCombine(name1: String, name2: String) {
    coroutineScope {
        val deferred1: Deferred<String> = async { loadImage(name1) }
        val deferred2: Deferred<String> = async { loadImage(name2) }
        println(deferred1.await() + deferred2.await())
    }
}

suspend fun loadImage(name: String): String {
    delay(5000)
    Log.info("loadImage ${name}")
    return name
}

fun main() = runBlocking<Unit> {
    this
    //依附到 runBlocking的协程作用域下
    Log.info("main before")
    Log.info("a:${this}")
    launch {
        Log.info("b: ${this.coroutineContext}")
        Log.info("in main coroutineScope launch")
        delay(200L)
        Log.info("Task from runBlocking")
    }

    // 创建一个协程作用域
    coroutineScope {
        launch {
            Log.info("in new coroutineScope launch")
            delay(500L)
            Log.info("Task from nested launch")
        }
        Log.info("at new coroutineScope")
        delay(100L)
        Log.info("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
    }

    Log.info("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
    Log.info("main after")

    loadAndCombine("image1", "image2")
}

