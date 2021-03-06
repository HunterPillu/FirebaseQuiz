package com.prinkal.quiz.ui.callbacks

interface ListItemClickListener<EVENT, MODEL> {
    fun onItemClick(type: EVENT, item: MODEL)
}