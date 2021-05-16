package com.jhapps.clien.storage

import android.content.Context
import android.content.SharedPreferences

class CookieSharedPref {


    private var pref:SharedPreferences? = null
    private constructor(context: Context){
        pref = context.getSharedPreferences("myCookies",Context.MODE_PRIVATE)
    }


    companion object{
        private var cookieSharedPref:CookieSharedPref? = null
        fun getInstance(context: Context):CookieSharedPref?{
            if(cookieSharedPref==null){
                cookieSharedPref = CookieSharedPref(context)
            }
            return cookieSharedPref
        }

    }



    private val key_sessionID = "sessionID"

    fun setSessionId(id:String){

        pref?.edit()?.let{
            it.putString(key_sessionID,id)
            it.apply()
        }

    }

    fun getSessionId():String?{
        return pref?.getString(key_sessionID,null)
    }

}