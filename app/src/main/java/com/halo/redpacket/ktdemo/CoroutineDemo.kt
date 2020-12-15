package com.halo.redpacket.ktdemo

import kotlinx.coroutines.*

class CoroutineDemo {
}

fun main(args: Array<String>) {
    //delay() 是最常见的挂起函数，类似于线程的 sleep() 函数。但 delay() 并不会阻塞线程。
    runBlocking {
        println("1: current thread is ${Thread.currentThread().name}")
        GlobalScope.launch {
            println("3: current thread is ${Thread.currentThread().name}")
            delay(1000L)
            println("5: current thread is ${Thread.currentThread().name}")
        }
        println("2: current thread is ${Thread.currentThread().name}")
        Thread.sleep(2000L)
        println("5: current thread is ${Thread.currentThread().name}")
    }

    //yield() 用于挂起当前的协程，将当前的协程分发到 CoroutineDispatcher 的队列，等其他协程的完成/挂起之后，再继续执行先前的协程。
    runBlocking {
        var job1 = launch {
            println(1)
            yield()
            println(3)
            yield()
            println(5)
        }
        val job2 = launch {
            println(2)
            yield()
            println(4)
            yield()
            println(6)
        }
        println(0)

        // 无论是否调用以下两句，上面两个协程都会运行
        job1.join()
        job2.join()
    }

    /**
     * Dispatchers 提供的多种 CoroutineDispatcher：
        Default 表示使用后台线程的公共线程池。
        IO 适用于 I/O 密集型的操作的线程池。
        Unconfined 表示在被调用的线程中启动协程，直到程序运行到第一个挂起点，协程会在相应的挂起函数所使用的任何线程中恢复。
     */
    GlobalScope.launch {
        //withContext 不会创建新的协程。 withContext 类似于 runBlocking，它的最后一行的值即为 withContext 的返回值：
        val result1 = withContext(Dispatchers.Default) {
            delay(2000)
            1
        }
        val result2 = withContext(Dispatchers.Default) {
            delay(1000)
            2
        }
        var  result = result1 + result2
        println(result)

        //跟 withContext 类似，coroutineScope 也会有返回值，但是 coroutineScope 采用父协程的 CoroutineContext，无法使用其他的 CoroutineDispatcher。
        val result3 = coroutineScope {
            delay(1000)
            3
        }
        result += result3
        println(result)
    }
    Thread.sleep(5000)

    /**
     * 协程拥有多种调度器。使用不同的协程调度器，协程会运行在不同的线程中。
     */
    runBlocking {

        val jobs = ArrayList<Job>()

        jobs += launch(Dispatchers.Unconfined) { // 无限制
            println("'Unconfined': I'm working in thread ${Thread.currentThread().name}")
        }

        jobs += launch(coroutineContext) { // 使用父级的上下文，也就是 runBlocking 的上下文
            println("'coroutineContext': I'm working in thread ${Thread.currentThread().name}")
        }

        jobs += launch(Dispatchers.Default) {
            println("'Dispatchers.Default': I'm working in thread ${Thread.currentThread().name}")
        }

        jobs += launch {
            println("'default': I'm working in thread ${Thread.currentThread().name}")
        }

        jobs += launch(newSingleThreadContext("MyThread")) { // 创建自己的新线程
            println("'MyThread': I'm working in thread ${Thread.currentThread().name}")
        }

        jobs.forEach { it.join() }
    }


    // 创建一个协程，并在内部再创建两个协程
    val job = GlobalScope.launch {

        // 第一个使用不同的上下文
        val job1 = GlobalScope.launch {

            println("job1: I have my own context and execute independently!")
            delay(1000)
            println("job1: I am not affected by cancellation of the job")
        }

        // 第二个继承父级上下文
        val job2 = launch(coroutineContext) {

            println("job2: I am a child of the job coroutine")
            delay(1000)
            println("job2: I will not execute this line if my parent job is cancelled")
        }

        job1.join()
        job2.join()
    }

    Thread.sleep(500)

    job.cancel() // 取消job

    Thread.sleep(2000)


    /**
     * 在 Android 开发中，Activity/Fragment 可以通过创建一个 Job 对象，
     * 并让该 Job 管理其他的协程。等到退出 Activity/Fragment 时，调用 job.cancel() 来取消协程的任务。
     */
    runBlocking<Unit> {

        val job = Job() // 创建一个 job 对象来管理生命周期

        launch(coroutineContext+job)  {

            delay(500)
            println("job1 is done")
        }

        launch(coroutineContext+job) {

            delay(1000)
            println("job2 is done")
        }

        launch(Dispatchers.Default+job) {

            delay(1500)
            println("job3 is done")
        }

        launch(Dispatchers.Default+job) {

            delay(2000)
            println("job4 is done")
        }

        launch(Dispatchers.Default+job) {

            delay(2500)
            println("job5 is done")
        }

        delay(1800)
        println("Cancelling the job!")
        job.cancel() // 取消任务
    }
}