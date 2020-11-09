package com.prinkal.quiz.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prinkal.quiz.data.api.ApiHelper
import com.prinkal.quiz.data.api.RetrofitBuilder
import com.prinkal.quiz.data.repository.MainRepository
import com.prinkal.quiz.ui.main.viewmodel.LoginViewModel
import com.prinkal.quiz.ui.main.viewmodel.MainViewModel

open class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(ApiHelper(RetrofitBuilder.apiService))) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}