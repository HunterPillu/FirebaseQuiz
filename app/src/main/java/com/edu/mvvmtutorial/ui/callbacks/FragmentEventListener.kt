package com.edu.mvvmtutorial.ui.callbacks

import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.base.BaseFragment
import com.edu.mvvmtutorial.utils.ConnectionLiveData


interface FragmentEventListener {
    fun updateToolbarTitle(title: String)
    fun openFragment(fragment: BaseFragment)
    fun onInviteOpponent(opponent: User)
    //fun getConnectionObject(): ConnectionLiveData
    fun firebaseInitGame()

}