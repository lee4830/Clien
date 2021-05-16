package com.jhapps.clien.activity

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.jhapps.clien.MainActivity
import com.jhapps.clien.R
import com.jhapps.clien.common.CommonDefine

class SplashActivity : CommonActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        goToMain()
    }


    private fun goToMain(){

        runWithHandler(Runnable {
            Intent(this@SplashActivity,MainActivity::class.java).run {
                startActivity(this)
            }
        },CommonDefine.DELAY_RUN)

    }







}