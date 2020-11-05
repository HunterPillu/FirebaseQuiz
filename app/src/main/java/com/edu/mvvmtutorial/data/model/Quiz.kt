package com.edu.mvvmtutorial.data.model

data class Quiz(
    var uid: String = "",
    var name: String = "",
    var description: String = "",
    var courseId: Int = 0,
    var gameRule: String = "",
    var abandonedScore: Int = 0,
    var disabled: Boolean = false,
    var winningScore: Int = 0,
    var questions: List<Question>? = null
)