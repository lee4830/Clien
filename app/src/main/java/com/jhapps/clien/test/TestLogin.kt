package com.jhapps.clien.test

import android.content.Context
import android.util.Log
import com.jhapps.clien.`interface`.ICommand
import com.jhapps.clien.`interface`.listener.ParsingListener
import com.jhapps.clien.common.beans.ParsingData
import com.jhapps.clien.storage.CookieSharedPref
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class TestLogin : ICommand {

    val LOG = this@TestLogin.javaClass.simpleName

    private var mUrlConnection: HttpsURLConnection? = null
    private var mUrl: String? = null
    private var mResponseCode: Int = 0
    private var mListener: ParsingListener? = null

    private var mThread: Thread? = null


    private var mContext:Context? = null


    private var redirectCount = 0

    private var mParams: String? = null

    constructor(context: Context?, url: String,params: String?, listener: ParsingListener?) {
        Log.d(LOG, "constructor called. url:[$url], listener:[$listener]")

        mContext = context
        mUrl = checkURL(url);

        mParams = params

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

                    //1. openConnection
                    openConnection()

                    mUrlConnection?.let { con ->



                        setCookieHeader(con)

                        setConnection(con)


                        //setParams
                        mParams?.let{
                            val arr = it.toByteArray(Charset.forName("UTF-8"))
                            try {
                                con.outputStream.write(arr)
                            } catch (e: Exception) {
                            }
                        }

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


    private fun setConnection(httpsURLConnection: HttpsURLConnection){
        httpsURLConnection.setHostnameVerifier(object : HostnameVerifier {
            override fun verify(hostname: String?, session: SSLSession?): Boolean {
                return true
            }
        })

        val trustAllCerts:Array<TrustManager> = arrayOf(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return null
            }

            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }
        }) 

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, SecureRandom())

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

        httpsURLConnection.doInput = true
        httpsURLConnection.doOutput = true
        httpsURLConnection.requestMethod = "POST"
        httpsURLConnection.readTimeout = 3000
        httpsURLConnection.connectTimeout = 3000
        httpsURLConnection.useCaches = false

        httpsURLConnection.setRequestProperty("User-Agent","Android")
        httpsURLConnection.setRequestProperty("Accept-Charset","UTF-8")
        httpsURLConnection.setRequestProperty("Context_Type","application/x-www-form-urlencoded;charset=UTF-8")



        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null,null,null)
        httpsURLConnection.sslSocketFactory = sslContext.socketFactory

    }

    private fun openConnection() {
        Log.d(LOG, "openConnection called.")




        val url = URL(mUrl)


        try {
            mUrlConnection = url.openConnection() as HttpsURLConnection
            Log.d(LOG, "openConnection succeed.")
        } catch (e: Exception) {
            Log.e(LOG, "openConnection failed. reason:[${e.message}]")
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

                mUrl = checkURL(con.getHeaderField("Location"))
                Log.d(LOG, "redirect newUrl:${mUrl}")
                try {
                    con.disconnect()
                } catch (e: Exception) {
                }

                if(redirectCount++<3){
                    runCommand()
                }else{
                    redirectCount = 0
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

}