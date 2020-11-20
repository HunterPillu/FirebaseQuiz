package com.prinkal.quiz.ui.callbacks

import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.base.BaseFragment


interface FragmentEventListener {
    fun openFragment(fragment: BaseFragment,canBeStacked:Boolean)
    fun onInviteOpponent(opponent: User, quizId: String)
    fun onCustomBackPressed()
    //fun getConnectionObject(): ConnectionLiveData
    fun firebaseInitGame()

}