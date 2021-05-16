package com.jhapps.clien.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.jhapps.clien.`interface`.ICommand
import com.jhapps.clien.`interface`.listener.ParsingListener
import com.jhapps.clien.common.beans.ParsingData
import com.jhapps.clien.storage.CookieSharedPref
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class TestParsing : ICommand {

    val LOG = this@TestParsing.javaClass.simpleName

    private var mUrlConnection: HttpsURLConnection? = null

    private var mUrl: String? = null
    private var mResponseCode: Int = 0
    private var mListener: ParsingListener? = null
    private var mThread: Thread? = null
    private var mContext:Context? = null
    private var redirectCount = 0



    constructor(context: Context?, url: String, listener: ParsingListener?) {
        Log.d(LOG, "constructor called. url:[$url], listener:[$listener]")

        mContext = context
        mUrl = url;

        mListener = listener

    }


    override fun runCommand() {
        Log.d(LOG, "runCommand called.")

        mThread?.let {
            it.interrupt()
            mThread = null
        }

        mThread = Thread(Runnable {

            try {
                while (!Thread.currentThread().isInterrupted) {
                    Log.d(LOG, "thread run")



                        setIgnoreSSL()


                    //1. openConnection
                    openConnection()


                        mUrlConnection?.let { con ->



                            setCookieHeader(con)

                            setConnection(con)


                            con.connect()
                            con.instanceFollowRedirects = true

                            mResponseCode = con.responseCode
                            Log.d(LOG, "mResponseCode:[$mResponseCode]")

                            startParsing(con)


                            Log.d(LOG, "thread is completed.")
                            //Thread 종료
                            Thread.currentThread().interrupt()
                        }




                }//while
            } catch (e: Exception) {

                Log.d(LOG, "thread is interrupted.")
            }

        })

        mThread?.start()


    }




    private fun setIgnoreSSL(){
        val trustAllCerts:Array<TrustManager> = arrayOf(object : X509TrustManager {

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return null
            }

            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }
        })

        try {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        } catch (e: Exception) {
        }
        try {

            val hv = object : HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                    return true
                }
            }

            HttpsURLConnection.setDefaultHostnameVerifier(hv)
        } catch (e: Exception) {
        }
    }

    private fun setConnection(httpsURLConnection: HttpsURLConnection){





        httpsURLConnection.doInput = true
//        httpsURLConnection.doOutput = true
        httpsURLConnection.requestMethod = "GET"
        httpsURLConnection.readTimeout = 3000
        httpsURLConnection.connectTimeout = 3000
        httpsURLConnection.useCaches = false
        httpsURLConnection.instanceFollowRedirects = true

        httpsURLConnection.setRequestProperty("User-Agent","Android")
//        httpsURLConnection.setRequestProperty("Accept-Charset","UTF-8")
//        httpsURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")



        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null,null,null)
        httpsURLConnection.sslSocketFactory = sslContext.socketFactory

    }



    private fun openConnection() {
        Log.d(LOG, "openConnection called.")

        val url = URL(mUrl)





            try {
                mUrlConnection = url.openConnection() as HttpsURLConnection
                Log.d(LOG, "https openConnection succeed.")
            } catch (e: Exception) {
                Log.e(LOG, " httpsopenConnection failed. reason:[${e.message}]")
            }




    }

    private fun startParsing(con: HttpsURLConnection) {
        Log.d(LOG, "startParsing called. mResponseCode:[$mResponseCode]")
        when (mResponseCode) {
            HttpsURLConnection.HTTP_OK -> {
                val bufferedReader = BufferedReader(InputStreamReader(con.inputStream))
                var line = bufferedReader.readLine()
                val stringBuilder = StringBuilder()
                while (line != null) {
                    stringBuilder.append(line)
                    line = bufferedReader.readLine()
                }

                getCookieHeader(con)
                mListener?.parsingComplete(
                    ParsingData(
                        mUrl?.toString(),
                        mResponseCode,
                        stringBuilder.toString()
                    )
                )
            }
            //Redirect 에 대한 처리.
            HttpsURLConnection.HTTP_MOVED_TEMP,
            HttpsURLConnection.HTTP_MOVED_PERM,
            HttpsURLConnection.HTTP_SEE_OTHER->{

                mUrl = con.getHeaderField("Location")

                Log.d(LOG, "redirect newUrl:${con.getHeaderField("Location")}")



                try {
                    con.disconnect()
                } catch (e: Exception) {
                }

                if(redirectCount++<3){
                    Handler(Looper.getMainLooper()).postDelayed({runCommand()},100)

                }else{
                    redirectCount = 0

                    Log.d(LOG, "redirect end.")
                }


            }
            else -> {

                val bufferedReader = BufferedReader(InputStreamReader(con.errorStream))

                var line = bufferedReader.readLine()
                val stringBuilder = StringBuilder()
                while (line != null) {
                    stringBuilder.append(line)
                    line = bufferedReader.readLine()
                }

                mListener?.parsingFailed(
                    ParsingData(
                        mUrl?.toString(),
                        mResponseCode,
                        stringBuilder.toString()
                    )
                )
            }
        }
    }




    private fun checkURL(url:String):String{
        var returnValue = url
        if(returnValue.contains("http://")){
            returnValue = returnValue.replace("http://","https://")
        }else if(!returnValue.contains("://")){
            returnValue = "https://"+returnValue
        }
        return returnValue
    }


    private fun setCookieHeader(httpsURLConnection: HttpsURLConnection){
        mContext?.let{
            mc->
            val sessionId = CookieSharedPref.getInstance(mc)?.getSessionId()
            if(sessionId!=null){
                httpsURLConnection.setRequestProperty("Cookie",sessionId)
                Log.d(LOG,"setCookieHeader:${sessionId}")
            }
        }

    }

    private fun setCookieHeader(httpURLConnection: HttpURLConnection){
        mContext?.let{
                mc->
            val sessionId = CookieSharedPref.getInstance(mc)?.getSessionId()
            if(sessionId!=null){
                httpURLConnection.setRequestProperty("Cookie",sessionId)
                Log.d(LOG,"setCookieHeader:${sessionId}")
            }
        }

    }

    private fun getCookieHeader(httpsURLConnection: HttpsURLConnection){
        val cookies = httpsURLConnection.getHeaderFields()["Set-Cookie"]
        cookies?.let{
            for(cookie in it){

                mContext?.let{
                    mc->
                    CookieSharedPref.getInstance(mc)?.setSessionId(cookie)

                    Log.d(LOG,"getCookieHeader:${cookie}")
                }

            }
        }
    }

    private fun getCookieHeader(httpURLConnection: HttpURLConnection){
        val cookies = httpURLConnection.getHeaderFields()["Set-Cookie"]
        cookies?.let{
            for(cookie in it){

                mContext?.let{
                        mc->
                    CookieSharedPref.getInstance(mc)?.setSessionId(cookie)

                    Log.d(LOG,"getCookieHeader:${cookie}")
                }

            }
        }
    }

}