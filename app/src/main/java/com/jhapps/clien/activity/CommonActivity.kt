package com.jhapps.clien.activity

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

open class CommonActivity : AppCompatActivity() {
    protected val handler = Handler(Looper.getMainLooper())


    protected fun runWithHandler(runnable: Runnable,delay:Long){
        handler.postDelayed(runnable,delay)

    }


    override fun onBackPressed() {
        super.onBackPressed()

        handler.let{
            try {
                it.removeCallbacksAndMessages(null)
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

}