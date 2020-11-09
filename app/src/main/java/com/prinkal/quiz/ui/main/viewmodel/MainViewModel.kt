package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.data.repository.MainRepository
import com.prinkal.quiz.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val users = MutableLiveData<Resource<List<User>>>()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        users.postValue(Resource.loading(null))
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val userList = mainRepository.getUsers()
                users.postValue(Resource.success(userList))
            } catch (e: Exception) {
                e.printStackTrace()
                users.postValue(Resource.error("Something Went Wrong", null))
            }
        }
        /*compositeDisposable.add(

                  mainRepository . getUsers ()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe({ userList ->
                   users.postValue(Resource.success(userList))
               }, { throwable ->
                   users.postValue(Resource.error("Something Went Wrong", null))
               })
        )*/
    }

    fun getUsers(): LiveData<Resource<List<User>>> {
        return users
    }

}