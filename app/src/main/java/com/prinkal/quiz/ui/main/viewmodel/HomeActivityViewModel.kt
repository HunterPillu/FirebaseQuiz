package com.prinkal.quiz.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.prinkal.quiz.R
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.Invite
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

    //private var dataListener: ListenerRegistration? = null
    private val TAG = HomeActivityViewModel::class.java.name

    //protected lateinit var connectionLiveData: ConnectionLiveData
    //private var callRef: DocumentReference? = null

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
        //callRef = FirebaseData.getPlayerReference(FirebaseData.myID)
        //dataListener = callRef?.addSnapshotListener(callListener)
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

        FirebaseData.setItem(opponent)

        viewModelScope.launch(Dispatchers.IO) {
            // set my game status to "WAITING"
            FirebaseApi.updateDbValue(
                hashMapOf<String, Any?>().apply {
                    this["status"] = Const.STATUS_WAITING
                    this["opponentId"] = ""
                    this["ts"] = Utils.getCurrentTimeInMillis()
                },
                FirebaseData.getPlayerReference(FirebaseData.myID)
            )

            //set opponent game status to "STATUS_INVITATION_RECEIVED"
            //save current user id to opponent's room so that he knows who is inviting him
            FirebaseApi.updateDbValue(
                hashMapOf<String, Any?>().apply {
                    this["status"] = Const.STATUS_INVITATION_RECEIVED
                    this["opponentId"] = FirebaseData.myID
                    this["ts"] = Utils.getCurrentTimeInMillis()
                },
                FirebaseData.getPlayerReference(opponent.uid)
            )


        }
    }

    /*private val callListener = object : EventListener<DocumentSnapshot> {
        override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
            if (e != null) {
                CustomLog.e(TAG, "Listen failed.", e)
                return
            }

            if (snapshot != null && snapshot.exists()) {
                CustomLog.d(TAG, "Current data: ${snapshot.data}")
                val invitation = snapshot.toObject(Invite::class.java)
                if (null != invitation
                    && invitation.status == Const.STATUS_INVITATION_RECEIVED
                //&& !Utils.hasInvitationExpired(invitation.ts)
                )
                    onInvitationReceived(invitation.opponentId)
            }
        }
    }*/

}