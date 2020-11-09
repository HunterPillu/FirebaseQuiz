package com.prinkal.quiz.ui.base

import androidx.lifecycle.ViewModel
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.main.viewmodel.InviteViewModel

class InviteViewModelFactory(private val player: User, private val quizId: String) :
    ViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InviteViewModel(player, quizId) as T
    }

}