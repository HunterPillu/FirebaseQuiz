package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.utils.Const
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Resource
import com.prinkal.quiz.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class InviteViewModel(private val player: User, private val quizId: String) :
    ViewModel() {

    private var isInvitationReceived: Boolean = false


    companion object {
        internal val TAG = InviteViewModel::class.java.name
    }

    private val data = MutableLiveData<Resource<Any>>()
    private val room = MutableLiveData<Resource<GameRoom>>()

    init {
        isInvitationReceived = quizId == ""
        if (isInvitationReceived) {
            listenForOpponentResponse()
        } else {
            createRoom()
        }
    }

    private fun createRoom() {
        //create room if this user is inviter

        viewModelScope.launch(Dispatchers.IO) {
            val room = GameRoom(FirebaseData.myID, "", Utils.getCurrentTimeInMillis())
            FirebaseApi.createRoom(room)
            listenForOpponentResponse()
        }
    }

    fun getData(): LiveData<Resource<Any>> {
        return data
    }

    fun getRoom(): LiveData<Resource<GameRoom>> {
        return room
    }


    @ExperimentalCoroutinesApi
    private fun listenForOpponentResponse() {
        val uid = if (isInvitationReceived) player.uid else FirebaseData.myID
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.listenGameRoomChange(uid).collect {
                CustomLog.e(TAG, "${it == null}")
                if (null != it) {
                    CustomLog.e(TAG, it.toString())
                    room.postValue(Resource.success(it))
                } else {
                    room.postValue(Resource.error("", null))
                }
            }
        }
    }

    fun invitationAccepted() {
        CustomLog.e(TAG, "invitationAccepted")
        viewModelScope.launch(Dispatchers.IO) {
            //set current user status from INVITATION_RECEIVED to IN_GAME
            val map = hashMapOf<String, Any?>()
            map["status"] = Const.STATUS_IN_GAME
            map["opponentId"] = ""
            //map["ts"] = 0
            FirebaseApi.updateUserField(map)

            // update room that opponent accepted
            FirebaseApi.updateRoomField(player.uid, "status", Const.STATUS_ACCEPTED)
        }
    }

    fun invitationRejected() {
        CustomLog.e(TAG, "invitationRejected")
        viewModelScope.launch(Dispatchers.IO) {
            //set current user status from INVITATION_RECEIVED to IDLE
            val map = hashMapOf<String, Any?>()
            map["status"] = Const.STATUS_IDLE
            map["opponentId"] = ""
            //map["ts"] = 0
            FirebaseApi.updateUserField(map)

            // update room that opponent accepted
            FirebaseApi.updateRoomField(player.uid, "status", Const.STATUS_REJECT)
        }
    }

    fun cancelInvitation() {
        CustomLog.e(TAG, "cancelInvitation")
        viewModelScope.launch(Dispatchers.IO) {
            //set current user status from WAITING to IDLE
            val map = hashMapOf<String, Any?>()
            map["status"] = Const.STATUS_IDLE
            map["opponentId"] = ""
            //map["ts"] = 0
            FirebaseApi.updateUserField(map)
            FirebaseApi.updateUserFieldById(player.uid, map)

            // update room that opponent accepted
            FirebaseApi.deleteRoom(FirebaseData.myID)
        }
    }


    // when reject the invitation then delete the ROOM and update the user status
    fun invitationRejectedByOpponent() {
        CustomLog.e(TAG, "invitationRejectedByOpponent")
        viewModelScope.launch(Dispatchers.IO) {
            //set current user status from WAITING to IDLE
            val map = hashMapOf<String, Any?>()
            map["status"] = Const.STATUS_IDLE
            map["opponentId"] = ""
            //map["ts"] = 0
            FirebaseApi.updateUserField(map)

            // delete room
            FirebaseApi.deleteRoom(FirebaseData.myID)
        }
    }

}