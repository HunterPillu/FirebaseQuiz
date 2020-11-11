package com.prinkal.quiz.ui.base

import androidx.lifecycle.ViewModel
import com.prinkal.quiz.ui.main.viewmodel.MultiQuizViewModel

class QuizViewModelFactory( private val roomId: String) :
    ViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MultiQuizViewModel(roomId) as T
    }

}