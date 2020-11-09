package com.prinkal.quiz.data.model

data class Course(
    var uid: String="",
    var name: String="",
    var desc: String="",
    var courseId: Int=0,
    var disabled: Boolean=false,
)