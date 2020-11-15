package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.utils.Const
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ResultMultiQuizViewModel(private val roomId: String) : ViewModel() {
    private val room = MutableLiveData<Resource<GameRoom>>()

    companion object {
        internal val TAG = ResultMultiQuizViewModel::class.java.name
    }

    init {
        CustomLog.d(TAG, "init")
        room.postValue(Resource.loading(null))
        listenGameRoom()
    }

    fun getRoom(): LiveData<Resource<GameRoom>> {
        return room
    }

    //listen GameRoom
    @ExperimentalCoroutinesApi
    private fun listenGameRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.listenGameRoomChange(roomId).collect {
                if (null != it) {
                    CustomLog.e(TAG, it.toString())
                    CustomLog.e(TAG, "STATUS : ${it.status}")
                    room.postValue(Resource.success(it))
                } else {
                    room.postValue(Resource.error("", null))
                }
            }
        }
    }

    fun updateRoomStatusToFinished() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.updateRoomField(roomId, "status", Const.STATUS_FINISHED)
        }
    }
}