package com.edu.mvvmtutorial.ui.callbacks

interface ListItemClickListener<EVENT, MODEL> {
    fun onItemClick(type: EVENT, item: MODEL)
}