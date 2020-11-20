package com.prinkal.quiz.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prinkal.quiz.ui.main.viewmodel.MultiQuizViewModel
import com.prinkal.quiz.ui.main.viewmodel.ResultMultiQuizViewModel

class QuizViewModelFactory(private val roomId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiQuizViewModel::class.java)) {
            return MultiQuizViewModel(roomId) as T
        } else if (modelClass.isAssignableFrom(ResultMultiQuizViewModel::class.java)) {
            return ResultMultiQuizViewModel(roomId) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}