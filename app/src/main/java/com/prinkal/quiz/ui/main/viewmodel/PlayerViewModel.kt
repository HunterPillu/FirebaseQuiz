package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.Quiz
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    val TAG = PlayerViewModel::class.java.name
    var isNetworkAvailable: Boolean = false

    //private lateinit var job: Job
    private val quiz = MutableLiveData<Resource<Quiz>>()
    private val userList = MutableLiveData<Resource<List<User>>>()

    init {
        fetchUsers()
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun listenForRoomEvents(uid:String){
        viewModelScope.launch {
            FirebaseApi.listenGameRoomChange(uid).collect {

            }
        }
    }

    private fun fetchUsers() {
        if (isNetworkAvailable) return
        CustomLog.d(TAG, "detching data")
        userList.postValue(Resource.loading(null))
        viewModelScope.launch {
            val data = FirebaseApi.fetchPlayers()
            userList.postValue(Resource.success(data))
        }
    }

    fun fetchQuiz(quizId: String) {
        quiz.postValue(Resource.loading(null))
        viewModelScope.launch {
            val data = FirebaseApi.getQuizWithQuestion(quizId)
            quiz.postValue(Resource.success(data))
        }
    }


    fun getQuiz(): LiveData<Resource<Quiz>> {
        return quiz
    }

    fun getUserList(): LiveData<Resource<List<User>>> {
        return userList
    }

}