package com.edu.mvvmtutorial.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.mvvmtutorial.data.api.FirebaseApi
import com.edu.mvvmtutorial.data.model.Course
import com.edu.mvvmtutorial.data.model.Quiz
import com.edu.mvvmtutorial.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private lateinit var job: Job
    private val quizList = MutableLiveData<Resource<List<Quiz>>>()
    private val courseList = MutableLiveData<Resource<List<Course>>>()

    init {
        //quizList.postValue(Resource.idle())
        fetchQuiz(-1)
        fetchCourse()
    }

    private fun fetchCourse() {
        courseList.postValue(Resource.loading(null))
        viewModelScope.launch {
            val data = FirebaseApi.fetchCourse()
            courseList.postValue(Resource.success(data))
        }
    }

    fun fetchQuiz(courseId: Int) {
        quizList.postValue(Resource.loading(null))
        viewModelScope.launch {
            val data = FirebaseApi.fetchQuiz(courseId)
            quizList.postValue(Resource.success(data))
        }
    }

    override fun onCleared() {
        super.onCleared()
        // job.cancel()
    }


    fun getQuizList(): LiveData<Resource<List<Quiz>>> {
        return quizList
    }

    fun getCourseList(): LiveData<Resource<List<Course>>> {
        return courseList
    }

}