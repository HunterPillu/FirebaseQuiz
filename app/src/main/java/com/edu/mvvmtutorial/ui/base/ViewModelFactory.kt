package com.edu.mvvmtutorial.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.edu.mvvmtutorial.data.api.ApiHelper
import com.edu.mvvmtutorial.data.api.RetrofitBuilder
import com.edu.mvvmtutorial.data.repository.MainRepository
import com.edu.mvvmtutorial.ui.main.viewmodel.LoginViewModel
import com.edu.mvvmtutorial.ui.main.viewmodel.MainViewModel

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(ApiHelper(RetrofitBuilder.apiService))) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}