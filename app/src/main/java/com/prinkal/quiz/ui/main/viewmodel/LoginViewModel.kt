package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private lateinit var job: Job
    private val loginResult = MutableLiveData<Resource<String>>()

    override fun onCleared() {
        super.onCleared()
        CustomLog.e(TAG, "onCleared")
    }

    companion object {
        internal val TAG = LoginViewModel::class.java.name
    }

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


    fun getLoginResult(): LiveData<Resource<String>> {
        return loginResult
    }

}