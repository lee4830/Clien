package com.jhapps.clien.test

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart


//Flow is like Obsevable
@ExperimentalCoroutinesApi
fun EditText.testChangesToFlow(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Unit
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                offer("offer:$s")

            }

            override fun afterTextChanged(s: Editable?) {

                Unit
            }

        }
        addTextChangedListener(listener)

        //메모리 해제시
        awaitClose {
            removeTextChangedListener(listener)
        }

    }.onStart {
        emit("emit:$text")
    }
}