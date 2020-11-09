package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prinkal.quiz.utils.Resource

class HomeActivityViewModel : ViewModel() {

    private val data = MutableLiveData<Resource<Any>>()

    init {
        data.postValue(Resource.idle())
    }

    fun getData(): LiveData<Resource<Any>> {
        return data
    }

}