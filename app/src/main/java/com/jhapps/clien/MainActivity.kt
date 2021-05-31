package com.jhapps.clien

import android.annotation.TargetApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.jhapps.clien.activity.CommonActivity
import com.jhapps.clien.common.CommonDefine
import com.jhapps.clien.fragments.DebugFragment

class MainActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
//            setupWindowAnimations()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        initView()

    }


    @Deprecated("Not Used")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun setupWindowAnimations(){
        with(window){
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = null
            exitTransition = null
        }
    }



    private fun initView(){
        if(CommonDefine.MODE_DEV){
            replaceFragment(DebugFragment())
        }else{
            //TODO 실제 구현시 동작

        }
    }


    private fun replaceFragment(fragment: Fragment){
        handler.postDelayed(Runnable {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.container_main,fragment)
                commitAllowingStateLoss()
            }
        },CommonDefine.DELAY_RUN)

    }


}