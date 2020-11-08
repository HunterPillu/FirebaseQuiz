package com.edu.mvvmtutorial.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edu.mvvmtutorial.utils.Resource

class HomeActivityViewModel : ViewModel() {

    private val data = MutableLiveData<Resource<Any>>()

    init {
        data.postValue(Resource.idle())
    }

    fun getData(): LiveData<Resource<Any>> {
        return data
    }

}