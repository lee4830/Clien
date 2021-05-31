package com.jhapps.clien.test

import android.util.Log
import com.jhapps.clien.activity.SplashActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TestCoroutine {

    private val LOG = TestCoroutine::class.java.simpleName


    private val defaultDispatcher = Dispatchers.Default
    private val scope = CoroutineScope(Dispatchers.Main)


    //메모리 관리를 위함
    private var myJob: Job = Job()
    private val myCoroutineContext:CoroutineContext
    get() = Dispatchers.IO + myJob

    fun test() {
        Log.d(LOG, "test start")

//        scope.launch {
//            fetchDocs()
//        }

//        scope.launch {
//            fetchTwoDocs()
//        }

//        scope.launch {
//            val temp = tempLoginReq("lee4830")
//
//            when(temp){
//                is Result.Success<String>->{
//                    Log.d(LOG,"temp:${temp.data}")
//                }
//                else->{
//                    Log.d(LOG,"error")
//                }
//            }
//
//            val temp2 = tempLoginReq("lee48301")
//
//
//            when(temp2){
//                is Result.Success<String>->{
//                    Log.d(LOG,"temp2:${temp2.data}")
//                }
//                else->{
//                    Log.d(LOG,"error")
//                }
//            }
//        }



//        val job1 = scope.launch {
//            delay(30000)
//            Log.d(LOG, "after 3000!")
//        }
//
//        val job2 = scope.launch {
//            delay(10000)
//            Log.d(LOG, "after 1000!")
//        }
//
//        job1.cancel()
//        job2.cancel()


        runBlocking {
            val a = launch {
                for(i in 1..5){
                    Log.d(LOG,"$i")
                    delay(10)
                }
            }

            val b = async {
                Log.d(LOG,"async 동작")
            }
            Log.d(LOG,"async 대기")
            Log.d(LOG,"${b.await()}")//완료대기 및 값 반환.
            Log.d(LOG,"launch 대기")
            a.join()//완료대기
            Log.d(LOG,"launch 종료")

        }



        runBlocking {
            val result = withTimeoutOrNull(130){
                for(i in 1..10){
                    Log.d(LOG,"$i")
                    delay(10)
                }
                "Finish"
            }

            Log.d(LOG,"$result")
        }


        GlobalScope.launch(myCoroutineContext){

        }

        //캔슬 (메모리해제)
        myCoroutineContext.cancel()


        Log.d(LOG, "test end")
    }


    sealed class Result<out R> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: java.lang.Exception) : Result<Nothing>()
    }


    suspend fun tempLoginReq(id: String): Result<String> {

        return withContext(defaultDispatcher) {
            when (id) {
                "lee4830" -> {
                    Result.Success("Succeed")
                }
                else -> {
                    Result.Error(java.lang.Exception("haha"))
                }
            }
        }

    }

    suspend fun fetchDocs() {
        val result = get("test.site")
        Log.d(LOG, "resut:$result")
    }

    private suspend fun get(url: String) = withContext(defaultDispatcher) {
        "OK"
    }


    suspend fun fetchTwoDocs() = coroutineScope {
        val firstTime = System.currentTimeMillis()
        Log.d(LOG, "fetchTwoDocs start")
        val deferredOne = async { one() }
        val deferredTwo = async { two() }

        deferredOne.await()
        deferredTwo.await()

        Log.d(LOG, "fetchTwoDocs end time:${System.currentTimeMillis() - firstTime}")

    }


    private fun one() =
        runBlocking {

            delay(1000)
            "One!"
        }

    private fun two() =
        runBlocking {
            delay(2000)
            "Two!"
        }

    fun testScope() {
        scope.launch {

            Log.d(LOG, "scope start")

            val firstTime = System.currentTimeMillis()
            Log.d(LOG, "time:${firstTime}")

            test2(firstTime)


            Log.d(LOG, "scope end")

        }
    }


    suspend fun test2(firstTime: Long) = withContext(defaultDispatcher) {
        delay(3000)
        Log.d(LOG, "different time:${System.currentTimeMillis() - firstTime}")
        Log.d(LOG, "defaultDispatcher is called.")
    }


    suspend fun temp(): String? {
        delay(3000)
        return "test await"
    }

    private fun basicCoroutine() {
        CoroutineScope(Dispatchers.IO).launch {

            val temp = async { temp() }
            temp.await()
            Log.d(LOG, "await end resut:$temp")

        }


        CoroutineScope(Dispatchers.Main).launch {
            //Main Thread
            Log.d(LOG, "Main Thread")
        }
        CoroutineScope(Dispatchers.IO).launch {
            //like networks, select database...
            Log.d(LOG, "like networks, select database...")

            //access Main thread
            withContext(Dispatchers.Main) {
                Log.d(LOG, "access Main thread...")
            }

            //TimeOut is null
            val result = withTimeoutOrNull(1000) {
                getCoroutineTest2()
            }

            if (result == null) {
                Log.d(LOG, "timeout")
            }

        }
        CoroutineScope(Dispatchers.Default).launch {
            //hard working
            Log.d(LOG, "hard working")
        }
    }

    //suspend : Asynchronous(비동기환경)
    suspend fun getCoroutineTest(): String {

        //Only in suspend
        delay(1100)

        return "Test"
    }

    fun getCoroutineTest2(): String {

        runBlocking {
            delay(1200)


        }

        return "Test"
    }


    private fun testObserver() {
        val observer: Observer<String> = object : Observer<String> {
            override fun onSubscribe(d: Disposable?) {
                Log.d(LOG, "onSubscribe:[$d]")
            }

            override fun onNext(t: String?) {
                Log.d(LOG, "onNext:[$t]")
                if (t.equals("null")) {
                    throw InterruptedException()
                }

            }

            override fun onError(e: Throwable?) {
                Log.d(LOG, "onError:$[${e?.message}]")

                when (e) {
                    InterruptedException::class.java -> {
                        Log.d(LOG, "onError:$[${e}]")
                    }
                    Exception::class.java -> {

                    }
                }
            }

            override fun onComplete() {
                Log.d(LOG, "onComplete")
            }

        }

        val obs1 = Observable.create<String> {
            it.onNext("testValue")
            it.onNext("testValue2")
            it.onNext("null")

            it.onComplete()
            it.setCancellable { }
        }

        //like network
        var sc = Schedulers.io()
        //main thread
//        sc = AndroidSchedulers.mainThread()
        obs1.subscribeOn(sc)

        obs1.subscribe(observer)


    }


    private fun test3() {
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>?) {

                emitter?.onNext("test")
                emitter?.onComplete()

            }
        }).run {


            subscribe(object : Observer<String> {
                override fun onComplete() {
                    Log.d(LOG, "onComplete")
                }

                override fun onError(e: Throwable?) {
                    Log.d(LOG, "onError")
                }

                override fun onNext(t: String?) {
                    Log.d(LOG, "onNext:$t")
                }

                override fun onSubscribe(d: Disposable?) {
                    Log.d(LOG, "onSubscribe:$d")
                }
            })
        }


    }

    fun test4() {
        val observable = Observable.create<Int> {

            try {
                runBlocking {
                    for (i in 1..5) {
                        Log.d(
                            LOG,
                            "rxText1 mainThread [${Thread.currentThread().name}] : onNext:$i"
                        )
                        it.onNext(i)
                        delay(1000)
                        if (i == 5) {
                            it.onComplete()
                        }

                    }
                }
            } catch (e: Exception) {
            }


        }

        observable
//            .subscribeOn(AndroidSchedulers.mainThread())//Observable이 동작하는 위치.
            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())//Observer가 동작하는 위치.
//            .doOnNext{i->

//                Log.d(LOG,"rxTest1 io [${Thread.currentThread().name}] : onNext:$i")
//            }
//            .observeOn(Schedulers.computation())
//            .doOnNext { i->
//                Log.d(LOG,"rxTest1 computation [${Thread.currentThread().name}] : onNext:$i")
//            }
            .observeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {


                override fun onSubscribe(d: Disposable?) {
                    Log.d(LOG, "rxTest1 onSubscribe ${Thread.currentThread().name} d:$d")

                }

                override fun onNext(t: Int?) {
                    Log.d(LOG, "rxTest1 onNext [${Thread.currentThread().name}] : onNext:$t")

                }

                override fun onError(e: Throwable?) {
                    Log.d(LOG, "rxTest1 onError : ${e?.message}")
                }

                override fun onComplete() {
                    Log.d(LOG, "rxTest1 onComplete")
                }

            })


    }


}