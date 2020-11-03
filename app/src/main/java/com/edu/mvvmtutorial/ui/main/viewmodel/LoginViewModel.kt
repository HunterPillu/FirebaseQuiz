package com.edu.mvvmtutorial.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.meripadhai.utils.Const.TABLE_USERS
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private lateinit var job: Job
    private val loginResult = MutableLiveData<Resource<String>>()

    init{
        loginResult.postValue(Resource.idle())
    }

    fun saveUser() {

        loginResult.postValue(Resource.loading(null))
        val fbUser = FirebaseAuth.getInstance().currentUser
        if (null != fbUser) {

            val user = User().apply {
                uid = fbUser.uid
                email = fbUser.email!!
                name=fbUser.displayName!!
            }

            job=viewModelScope.launch {
                if(createUser(user)){
                    loginResult.postValue(Resource.success(null))
                }else{
                    loginResult.postValue(Resource.error("",null))
                }
            }

        }else{
            loginResult.postValue(Resource.error("",null))
        }


    }

    suspend fun createUser(user: User) : Boolean{

        return try{
            Firebase.firestore
                .collection(TABLE_USERS)
                .document(user.uid)
                .set(user)
                .await()
            true
        }catch (e : Exception){
            false
        }
    }

    fun getLoginResult(): LiveData<Resource<String>> {
        return loginResult
    }

}