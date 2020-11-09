package com.prinkal.quiz.ui.callbacks

interface GameEventListener {
    fun onParticipantInvited()
    fun onParticipantJoined()
    fun onParticipantLeave()
    fun onParticipantRejected()
}