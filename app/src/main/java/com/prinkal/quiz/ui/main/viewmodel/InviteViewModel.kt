package com.prinkal.quiz.ui.main.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.GameRoom
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class InviteViewModel(private val player: User, private val quizId: String) :
    ViewModel() {

    private var isInvitationReceived: Boolean = false
    private val mElapsedTime = MutableLiveData<Long>()
    private var timer: CountDownTimer? = null


    companion object {
        internal val TAG = InviteViewModel::class.java.name
    }

    private val room = MutableLiveData<Resource<GameRoom>>()

    init {
        room.postValue(Resource.loading(null))
        isInvitationReceived = quizId == ""
        if (isInvitationReceived) {
            listenForOpponentResponse()
        } else {
            createRoom()
            // start and show timer if user sent the invitation
            startTimer()
        }
    }


    //start timer for game invitation response
    private fun startTimer() {
        timer = object : CountDownTimer(Config.INVITATION_EXPIRE_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mElapsedTime.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                mElapsedTime.value = 0
                room.postValue(Resource.error("", null))
            }
        }
        timer?.start()
    }

    fun getElapsedTime(): LiveData<Long> {
        return mElapsedTime
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimer()
    }

    private fun createRoom() {
        //create room if this user is inviter

        viewModelScope.launch(Dispatchers.IO) {
            val creator = FirebaseApi.getUserById(FirebaseData.myID)!!

            val room = GameRoom(
                creator.uid,
                creator.name,
                player.uid,
                player.name,
                quizId,
                Utils.getCurrentTimeInMillis()
            )
            FirebaseApi.createRoom(room)
            listenForOpponentResponse()
        }
    }

    fun getRoom(): LiveData<Resource<GameRoom>> {
        return room
    }


    //listen GameRoom for opponent response
    @ExperimentalCoroutinesApi
    private fun listenForOpponentResponse() {
        val uid = if (isInvitationReceived) player.uid else FirebaseData.myID
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirebaseApi.listenGameRoomChange(uid).collect {
                    CustomLog.e(TAG, "${it == null}")
                    if (null != it) {
                        CustomLog.e(TAG, it.toString())
                        room.postValue(Resource.success(it))
                    } else {
                        room.postValue(Resource.error("", null))
                    }
                }
            } catch (e: Exception) {
                CustomLog.e(TAG, e)
            }
        }
    }

    fun invitationAccepted() {
        CustomLog.e(TAG, "invitationAccepted")
        room.postValue(Resource.loading(null))
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

    private fun cancelTimer() {
        timer?.cancel()
    }

    fun invitationRejected() {
        CustomLog.d(TAG, "invitationRejected")

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
        CustomLog.d(TAG, "cancelInvitation")

        cancelTimer()

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

    fun getRoomId(): String {
        return if (isInvitationReceived) player.uid else FirebaseData.myID
    }

}