package com.jhapps.clien.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.jhapps.clien.MainActivity
import com.jhapps.clien.R
import com.jhapps.clien.common.CommonDefine
import com.jhapps.clien.common.SiteDefine
import io.reactivex.rxjava3.core.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Runnable
import java.nio.charset.Charset


class SplashActivity : CommonActivity() {

    companion object {
        private val LOG = SplashActivity::class.java.simpleName
    }

    //Coroutine 사용을 위함.
    private val dispatcher = Dispatchers.Main
    private val scope = CoroutineScope(dispatcher)
    private var initJob: Job? = null


    /* 오버라이드 된 함수  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


//        Intent(this,TestViewModelActivity::class.java).run {
//            startActivity(this)
//        }

//        loadDefaultInfo()


        scope.launch {

            var num = 0
            val one = 1
            val zero = 0
            Log.d("test123","$num AND $one = ${(num and one)}")
            Log.d("test123","$num AND $zero = ${(num and zero)}")

            Log.d("test123","$num OR $one = ${(num or one)}")
            Log.d("test123","$num OR $zero = ${(num or zero)}")

            Log.d("test123","$num XOR $one = ${(num xor one)}")
            Log.d("test123","$num XOR $zero = ${(num xor zero)}")

            num = 1
            Log.d("test123","$num AND $one = ${(num and one)}")
            Log.d("test123","$num AND $zero = ${(num and zero)}")

            Log.d("test123","$num OR $one = ${(num or one)}")
            Log.d("test123","$num OR $zero = ${(num or zero)}")

            Log.d("test123","$num XOR $one = ${(num xor one)}")
            Log.d("test123","$num XOR $zero = ${(num xor zero)}")

            num = 2
            Log.d("test123","$num AND $one = ${(num and one)}")
            Log.d("test123","$num AND $zero = ${(num and zero)}")

            Log.d("test123","$num OR $one = ${(num or one)}")
            Log.d("test123","$num OR $zero = ${(num or zero)}")

            Log.d("test123","$num XOR $one = ${(num xor one)}")
            Log.d("test123","$num XOR $zero = ${(num xor zero)}")




        }



    }


    override fun onDestroy() {
        super.onDestroy()
        //종료되지 않은 작업이나 사용자에 의해 종료된 경우 Scope 종료.
        try {
            initJob?.cancel()
        } catch (e: Exception) {
        }
    }

    /* 정의 이벤트 */

    private fun loadDefaultInfo() {

        scope.launch {

            val json = scope.async {
                val inputStream = resources.openRawResource(R.raw.site_info)
                val reader =
                    BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
                val strBuilder = StringBuilder()
                var line: String? = null
                do {
                    line = reader.readLine()
                    if (line != null)
                        strBuilder.append(line)
                } while (line != null)

                Log.d(LOG, "strBuilder=[${strBuilder.toString()}]")

                try {
                    inputStream.close()
                } catch (e: Exception) {
                    e.stackTrace
                }


                strBuilder.toString()


            }
            json.await()

            val jsonObject = JSONObject(json.toString())
            val version = jsonObject.getString("version")


            SiteDefine.BASE_URL = jsonObject.getString("base_url")

            SiteDefine.LOGIN_URL = jsonObject.getString("login_url")

            SiteDefine.ROOT_URL = jsonObject.getString("root_url")

            Log.d(
                LOG,
                "version=[$version] base_url=[${SiteDefine.BASE_URL}] login_url=[${SiteDefine.LOGIN_URL}] root_url=[${SiteDefine.ROOT_URL}]"
            )


            goToMain()

        }//outer


    }


    //메인 화면 이동
    private fun goToMain() = runWithHandler(Runnable {
        Intent(this@SplashActivity, MainActivity::class.java).run {
            startActivity(this)
            overridePendingTransition(0, 0)
        }
    }, CommonDefine.DELAY_RUN_1500)


}