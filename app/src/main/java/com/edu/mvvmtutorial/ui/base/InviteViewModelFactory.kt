package com.edu.mvvmtutorial.ui.base

import androidx.lifecycle.ViewModel
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.main.viewmodel.InviteViewModel

class InviteViewModelFactory(private val player: User, private val quizId: String) :
    ViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InviteViewModel(player, quizId) as T
    }

}