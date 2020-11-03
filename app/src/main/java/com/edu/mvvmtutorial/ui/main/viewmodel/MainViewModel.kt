package com.edu.mvvmtutorial.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.data.repository.MainRepository
import com.edu.mvvmtutorial.utils.Resource
import io.reactivex.disposables.CompositeDisposable
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

    override fun onCleared() {
        super.onCleared()
    }

    fun getUsers(): LiveData<Resource<List<User>>> {
        return users
    }

}