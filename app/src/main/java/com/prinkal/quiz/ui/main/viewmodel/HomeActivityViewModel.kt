package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.prinkal.quiz.R
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.api.RetrofitBuilder
import com.prinkal.quiz.data.model.Invite
import com.prinkal.quiz.data.model.PQData
import com.prinkal.quiz.data.model.PQNotification
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeActivityViewModel : ViewModel() {

    private val data = MutableLiveData<Resource<Any>>()
    private val invitation = MutableLiveData<Invitation<User>>()

    private val TAG = HomeActivityViewModel::class.java.name

    //protected lateinit var connectionLiveData: ConnectionLiveData

    init {
        data.postValue(Resource.idle())
        firebaseInitGame()
    }

    fun getData(): LiveData<Resource<Any>> {
        return data
    }

    fun getInvitation(): LiveData<Invitation<User>> {
        return invitation
    }

    //automatic initialize Firebase database for game event listener : Game-Invitation
    fun firebaseInitGame() {
        if (null == Firebase.auth.currentUser) {
            return
        }
        // initialize Firebase variables
        FirebaseData.init()
        //listen for game invitation
        listenForInvitation()

    }

    //listen GAME_STATUS change on USER table
    @ExperimentalCoroutinesApi
    private fun listenForInvitation() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseApi.listenUserChange<Invite>(FirebaseData.myID).collect {
                if (null != it) {
                    CustomLog.e(TAG, it.toString())
                    if (it.status == Const.STATUS_INVITATION_RECEIVED
                    //&& !Utils.hasInvitationExpired(it.ts)
                    ) {
                        invitation.postValue(Invitation.receiveInvite(it.opponentId))
                    }
                }
            }
        }
    }

    fun addSnapshotListener() {
        //dataListener = callRef?.addSnapshotListener(callListener)
    }

    fun removeSnapshotListener() {
        //dataListener?.remove()
    }


    //when user click a user item then send invite
    fun onInviteOpponent(opponent: User, quizId: String) {
        //check for valid invite
        //can invite if player offline
        if (!Const.CAN_REQUEST_IF_OFFLINE && !opponent.online) {
            invitation.postValue(Invitation.error(R.string.pq_user_offline, opponent.name))
            return
        }

        //check if player is already in game
        if (opponent.status == Const.STATUS_IN_GAME) {
            invitation.postValue(Invitation.error(R.string.pq_user_in_game, opponent.name))
            return
        }

        //check if someone else has already requested a invitation
        if (opponent.status == Const.STATUS_INVITATION_RECEIVED
            && Utils.hasInvitationExpired(opponent.ts)
        ) {
            invitation.postValue(Invitation.error(R.string.pq_user_invited, opponent.name))
            return
        }

        //check if player is already in game
        if (opponent.status == Const.STATUS_WAITING &&
            Utils.hasInvitationExpired(opponent.ts)
        ) {
            invitation.postValue(Invitation.error(R.string.pq_user_waiting, opponent.name))
            return
        }

        //show dialog
        invitation.postValue(Invitation.sendInvite(opponent, quizId))

        viewModelScope.launch(Dispatchers.IO) {
            // set my game status to "WAITING"
            FirebaseApi.updateUserFieldById(
                FirebaseData.myID,
                hashMapOf<String, Any?>().apply {
                    this["status"] = Const.STATUS_WAITING
                    this["opponentId"] = ""
                    this["ts"] = Utils.getCurrentTimeInMillis()
                }
            )

            //set opponent game status to "STATUS_INVITATION_RECEIVED"
            //save current user id to opponent's room so that he knows who is inviting him
            FirebaseApi.updateUserFieldById(
                opponent.uid,
                hashMapOf<String, Any?>().apply {
                    this["status"] = Const.STATUS_INVITATION_RECEIVED
                    this["opponentId"] = FirebaseData.myID
                    this["ts"] = Utils.getCurrentTimeInMillis()
                }
            )
        }
    }

    fun sendInviteNotification(firebaseToken: String?, title: String, body: String) {
        if (null == firebaseToken) return
        viewModelScope.launch(Dispatchers.IO) {
            val data = PQData(title, body, FirebaseData.myID)
            val notificationModel = PQNotification(to = firebaseToken, data = data)
            try {
                val result = RetrofitBuilder.apiService.sendInviteNotification(notificationModel)
                if (null != result) {
                    CustomLog.e(TAG, result.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CustomLog.e(TAG, e)
            }
        }
    }


}