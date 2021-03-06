package com.prinkal.quiz.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes

@IntDef(Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
private annotation class ToastLength

fun shortToast(context: Context, @StringRes text: Int) {
    shortToast(context, context.getString(text))
}

fun showMsg(context: Context, @StringRes text: Int) {
    shortToast(context, context.getString(text))
}
fun showMsg(context: Context, text: String) {
    shortToast(context, text)
}

fun shortToast(context: Context, text: String) {
    show(context, text, Toast.LENGTH_SHORT)
}

/*fun longToast(@StringRes text: Int) {
    longToast(MainApp.getInstance().getString(text))
}

fun longToast(text: String) {
    show(text, Toast.LENGTH_LONG)
}*/

private fun makeToast(context: Context, text: String, @ToastLength length: Int): Toast {
    return Toast.makeText(context, text, length)
}

private fun show(context: Context, text: String, @ToastLength length: Int) {
    makeToast(context, text, length).show()
}