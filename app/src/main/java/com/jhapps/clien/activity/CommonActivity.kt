package com.jhapps.clien.activity

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

open class CommonActivity : AppCompatActivity(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    protected val handler = Handler(Looper.getMainLooper())


    protected fun runWithHandler(runnable: Runnable, delay: Long) {
        handler.postDelayed(runnable, delay)

    }


    override fun onDestroy() {
        super.onDestroy()

        //뒤로가기 종료시 핸들러 콜백 종료.
        handler.run {
            try {
                removeCallbacksAndMessages(null)
            } catch (e: Exception) {
                e.stackTrace
            }
        }

        try {
            job.cancel()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

}