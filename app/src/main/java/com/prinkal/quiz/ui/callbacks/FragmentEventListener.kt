package com.prinkal.quiz.ui.callbacks

import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.base.BaseFragment


interface FragmentEventListener {
    fun updateToolbarTitle(title: String)
    fun openFragment(fragment: BaseFragment)
    fun onInviteOpponent(opponent: User, quizId: String)
    fun onBackPressed()
    //fun getConnectionObject(): ConnectionLiveData
    fun firebaseInitGame()

}