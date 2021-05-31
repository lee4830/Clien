package com.jhapps.clien.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.jhapps.clien.MainActivity
import com.jhapps.clien.R
import com.jhapps.clien.`interface`.listener.ParsingListener
import com.jhapps.clien.common.beans.ParsingData
import com.jhapps.clien.test.TestCoroutine
import com.jhapps.clien.test.TestLogin
import com.jhapps.clien.test.TestParsing
import com.jhapps.clien.test.testChangesToFlow
import kotlinx.android.synthetic.main.fragment_debug.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.lang.StringBuilder
import java.net.URLEncoder

class DebugFragment : Fragment() {

    private var testUrl = "https://m.clien.net/service/auth/login"
    private var loginUrl = "https://m.clien.net/service/login"

    private var jangterUrl =
        "https://m.clien.net/service/board/sold/16143979?od=T31&po=0&category=0&groupCd="

    private var csrf = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_debug, container, false)





        val editText = v.findViewById<EditText>(R.id.testEditText)

        GlobalScope.launch(Dispatchers.IO) {
            val flow = editText.testChangesToFlow()

            flow
                .debounce(100)//딜레이
                .onEach {
                    Log.d("test123", "flow로 받음 : ${it}")
                }
                .launchIn(this)
        }

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        TestCoroutine().run {
            test()
        }


        testText.movementMethod = ScrollingMovementMethod()

        testButton.setOnClickListener {

//            testUrl = "https://m.naver.com"

            TestParsing(context, testUrl, object : ParsingListener {
                override fun parsingComplete(data: ParsingData) {

                    val contents = data.content

                    Log.d("test123", "parsingComplete")
                    Log.d("test123", "requestURl:${data.url}")
                    Log.d("test123", "responseCode:${data.responseCode}")
                    Log.d("test123", "content:$contents")

                    val doc = Jsoup.parse(contents)

                    val doc_csrf = doc.select("input [name=_csrf]")

                    val csrf = doc_csrf.attr("value")

                    Handler(Looper.getMainLooper()).post {
                        testText.text = csrf
                    }


//                    //로그인 value 기준 값.
//                    val ref_str = "name=\"_csrf\" value=\""
//                    var startIndex =contents?.indexOf(ref_str)
//                    if(startIndex!=null&&startIndex>=0){
//                        var tempLog0 = contents!!.substring(startIndex,startIndex+100)
//                        Log.d("test123","tempLog pre:$tempLog0")
//
//                        //기존 인덱스에 (글자 수 - 1) 을 더한 값.
//                        startIndex += ref_str.length - 1
//                        var tempStr = contents!!.substring(startIndex+1)
//
//                        val endTempStr = tempStr.indexOf("\"")
//                        val tempLog = tempStr.substring(0,endTempStr)
//                        testText.text = tempLog
//
//                        csrf = tempLog
//
//                        Log.d("test123","tempLog post:$tempLog")
//                    }


                }

                override fun parsingFailed(data: ParsingData) {
                    Log.d("test", "parsingFailed")
                    Log.d("test123", "requestURl:${data.url}")
                    Log.d("test123", "responseCode:${data.responseCode}")
                    Log.d("test123", "content:${data.content}")
                }
            }).run {
                runCommand()
            }
        }



        testButton2.setOnClickListener {

            var params = LinkedHashMap<String, String>()
            params.put("userId", "lee4830")
            params.put("userPassword", "dntrltlsp1a")
            params.put("_csrf", csrf)

            val postData = StringBuilder()
            var itFirst = true
            for (p in params) {
                if (itFirst) {
                    itFirst = false
                } else {
                    postData.append("&")
                }
                postData.append(URLEncoder.encode(p.key, "UTF-8"))
                postData.append("=")
                postData.append(URLEncoder.encode(p.value, "UTF-8"))

            }
            val p = postData.toString()
            Log.d("test123", "params:${p}")
            TestLogin(context, loginUrl, p, object : ParsingListener {
                override fun parsingComplete(data: ParsingData) {

                    val contents = data.content

                    Log.d("test123", "parsingComplete")
                    Log.d("test123", "requestURl:${data.url}")
                    Log.d("test123", "responseCode:${data.responseCode}")
                    Log.d("test123", "content:$contents")

                    Log.d("test123", "TestLogin parsingComplete:${contents}")

                }

                override fun parsingFailed(data: ParsingData) {
                    Log.d("test123", "TestLogin parsingFailed:${data}")
                }
            }).run {
                runCommand()
            }


        }


        testButton3.setOnClickListener {
            TestParsing(context, jangterUrl, object : ParsingListener {
                override fun parsingComplete(data: ParsingData) {

                    val contents = data.content

                    Log.d("test123", "parsingComplete")
                    Log.d("test123", "requestURl:${data.url}")
                    Log.d("test123", "responseCode:${data.responseCode}")
                    Log.d("test123", "content:$contents")

                    Log.d("test123", "TestLogin parsingComplete:${contents}")


                }

                override fun parsingFailed(data: ParsingData) {
                    Log.d("test", "parsingFailed")
                    Log.d("test123", "requestURl:${data.url}")
                    Log.d("test123", "responseCode:${data.responseCode}")
                    Log.d("test123", "content:${data.content}")
                }
            }).run {
                runCommand()
            }
        }
    }

}