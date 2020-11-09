package com.edu.mvvmtutorial.ui.callbacks

import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.base.BaseFragment


interface FragmentEventListener {
    fun updateToolbarTitle(title: String)
    fun openFragment(fragment: BaseFragment)
    fun onInviteOpponent(opponent: User, quizId: String)

    //fun getConnectionObject(): ConnectionLiveData
    fun firebaseInitGame()

}