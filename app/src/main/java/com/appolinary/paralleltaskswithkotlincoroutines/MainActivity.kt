package com.appolinary.paralleltaskswithkotlincoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            setNewText("Clicked!")

            fakeApiRequest()

        }
    }

    //  case2 можно сказать что Deferred это Job которая возвращает результат (Deferred value is a non-blocking cancellable future — it is a Job that has a result.)
    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async{
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                val result2: Deferred<String> = async{
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromApi()
                }

                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")
            }
            println("debug: total time elapsed: ${executionTime}")
        }
    }


//    case1
//    private fun fakeApiRequest() {
//
//        val startTime = System.currentTimeMillis()
//
//        val parentJob = CoroutineScope(IO).launch {
//            val job1 = launch {
//                val time1 = measureTimeMillis {
//                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
//                    val result1 = getResult1FromApi()
//                    setTextOnMainThread("Got $result1")
//                }
//                println("debug: completed job1 in $time1 ms ")
//            }
////            job1.join() - если добавить эту строку, то выполнение программы остановится здесь и будет ждать, пока job1 будет выполнена
////            а без этой строки job1 и job2 будут выполняться параллельно
//
//            val job2 = launch {
//                val time2 = measureTimeMillis {
//                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
//                    val result2 = getResult2FromApi()
//                    setTextOnMainThread("Got $result2")
//                }
//                println("debug: completed job2 in $time2 ms ")
//            }
//        }
//
//        parentJob.invokeOnCompletion {// - invokeOnCompletion == вызов по окончании выполнения parentJob
//            println("debug: total elapsed time: ${System.currentTimeMillis() - startTime}")
//        }
//    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1700)
        return "Result #2"
    }
}
