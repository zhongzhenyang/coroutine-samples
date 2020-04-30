package demo.coroutine.context


import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.coroutines.coroutineContext

val Log = LoggerFactory.getLogger("coroutine-context")

var cs0: CoroutineScope? = null
var cs1: CoroutineScope? = null
var cs2: CoroutineScope? = null
var cs3: CoroutineScope? = null

fun main() = runBlocking<Unit> {
    cs0 = this

    Log.info("A context with name: ${coroutineContext + CoroutineName("test")}")

    // A current job can be retrieved from a current coroutine’s context:
    Log.info("My job is: ${coroutineContext[Job]}")

    val job: Job = launch(CoroutineName("child")) {
        cs1 = this
        Log.info("My context is $coroutineContext}")
        //equal
        Log.info("parent context contains the job: ${cs0?.coroutineContext?.get(Job)?.children?.firstOrNull() == coroutineContext[Job]}")
    }

    Log.info("job:${job}")

    launch {
        cs2 = this
        scopeCheck(this)
    }
    //如果用 coroutineScope builder,cs1,cs2会持有. 如果用 launch builder 将会释放 cs1, cs2, cs3.cs1 == cs2 ==cs3 == null
    coroutineScope {
        cs3 = this
        delay(100)
    }

    println("cs1 is cs2:${cs1 == cs2}")
    Log.info("cs0: ${cs0?.toString()}")
    Log.info("cs1: ${cs1?.toString()}")
    Log.info("cs2: ${cs2?.toString()}")
    Log.info("cs3: ${cs3?.toString()}")


    /*
     * 但是如果提取出的函数包含一个在当前作用域中调用的协程构建器的话，该怎么办？
     * 在这种情况下，所提取函数上只有 suspend 修饰符是不够的。
     * 为 CoroutineScope 写一个 doThis 扩展方法是其中一种解决方案，但这可能并非总是适用，因为它并没有使 API 更加清晰。
     * 惯用的解决方案是要么显式将 CoroutineScope 作为包含该函数的类的一个字段， 要么当外部类实现了 CoroutineScope 时隐式取得。
     * 作为最后的手段，可以使用 CoroutineScope(coroutineContext)，不过这种方法结构上不安全， 因为你不能再控制该方法执行的作用域。只有私有 API 才能使用这个构建器。
     */
    doThis()
    doThatIn(this)
//    println("cs2 is cs3:${cs2 == cs3}")
}

suspend fun scopeCheck(scope: CoroutineScope) {
    println(scope.coroutineContext === coroutineContext)
}



/**
    Do not make these functions suspending:
 *  为 CoroutineScope 写一个 doThis 扩展方法是其中一种解决方案，但这可能并非总是适用，因为它并没有使 API 更加清晰。
 */
fun CoroutineScope.doThis() {
    launch { println("I'm fine") }
}

fun doThatIn(scope: CoroutineScope) {
    scope.launch { println("I'm fine, too") }
}

/**
 * Do not do this! It makes the scope in which the coroutine is launched opaque and implicit,
 * capturing some outer Job to launch a new coroutine without explicitly announcing it in the function signature.
 */
suspend fun doNotDoThis() {
    CoroutineScope(coroutineContext).launch {
        println("I'm confused")
    }
}
