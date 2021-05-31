package com.jhapps.clien.test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import com.jhapps.clien.R
import kotlinx.android.synthetic.main.activity_test_view_model.*

class TestViewModelActivity : AppCompatActivity() {


//    private lateinit var viewModel: NameViewModel

//    private val viewModel:NameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view_model)


//        viewModel = ViewModelProvider(this, NameViewModelFactory()).get(NameViewModel::class.java)
//        viewModel = ViewModelProvider(this).get(NameViewModel::class.java)

        val viewModel: NameViewModel by viewModels()

        viewModel.currentName.observe(this, Observer<String> {
            testTextView.text = it
        })

        testButton.setOnClickListener {
            val input = testEditText.text.toString()
            viewModel.updateValue(input)
        }



    }






    //커스텀이 필요한 경우.
    class NameViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            try {
                return modelClass.newInstance()
            } catch (e: Exception) {
                throw RuntimeException(e.message)
            }
        }

    }


    class NameViewModel : ViewModel() {

        private val _currentName = MutableLiveData<String>()
        val currentName: LiveData<String>
            get() = _currentName

        init {
            _currentName.value = ""
        }

        fun updateValue(str: String) {
            _currentName.value += "\n$str"
        }

    }

    class NameViewModel2 : ViewModel() {
        private val currentName: MutableLiveData<String> by lazy {
            MutableLiveData<String>().also {
                it.value = getName()
            }
        }

        fun setCurrentName(name: String) {
            currentName.value = name
        }

        fun getCurrentName(): LiveData<String> {
            return currentName
        }

        private fun getName(): String {
            return "Test"
        }

    }


    class TempFragment: Fragment(){

        //방법1
        private val model: NameViewModel by activityViewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            //방법2
            val viewModel = ViewModelProvider(requireActivity()).get(NameViewModel::class.java)

        }

    }

}