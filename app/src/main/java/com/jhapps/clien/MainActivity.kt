package com.jhapps.clien

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import androidx.fragment.app.Fragment
import com.jhapps.clien.activity.CommonActivity
import com.jhapps.clien.common.CommonDefine
import com.jhapps.clien.fragments.DebugFragment

class MainActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView(){
        if(CommonDefine.MODE_DEV){
            replaceFragment(DebugFragment())
        }else{

        }
    }


    private fun replaceFragment(fragment: Fragment){
        handler.postDelayed(Runnable {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.container_main,fragment)
                commitNow()
            }
        },CommonDefine.DELAY_RUN)

    }


}