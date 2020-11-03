package com.edu.mvvmtutorial.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.mvvmtutorial.data.api.FirebaseApi
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private lateinit var job: Job
    private val loginResult = MutableLiveData<Resource<String>>()

    init {
        loginResult.postValue(Resource.idle())
    }

    fun saveUser() {

        loginResult.postValue(Resource.loading(null))
        val fbUser = FirebaseAuth.getInstance().currentUser
        if (null != fbUser) {
            val user = User().apply {
                uid = fbUser.uid
                email = fbUser.email!!
                name = fbUser.displayName!!
            }

            job = viewModelScope.launch {
                if (FirebaseApi.createUser(user)) {
                    loginResult.postValue(Resource.success(null))
                } else {
                    loginResult.postValue(Resource.error("", null))
                }
            }
        } else {
            loginResult.postValue(Resource.error("", null))
        }


    }

    override fun onCleared() {
        super.onCleared()
        // job.cancel()
    }


    fun getLoginResult(): LiveData<Resource<String>> {
        return loginResult
    }

}