package com.edu.mvvmtutorial.ui.callbacks

interface GameEventListener {
    fun onParticipantInvited()
    fun onParticipantJoined()
    fun onParticipantLeave()
    fun onParticipantRejected()
}