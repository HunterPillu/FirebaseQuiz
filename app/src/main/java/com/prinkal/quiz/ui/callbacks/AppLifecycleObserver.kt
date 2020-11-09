package com.prinkal.quiz.ui.callbacks


import android.content.Context
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.prinkal.quiz.R
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.utils.CustomLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Displays a message when app comes to foreground and goes to background.
 */
class AppLifecycleObserver constructor(context: Context) : LifecycleObserver {

    private val enterForegroundToast =
        Toast.makeText(context, context.getString(R.string.foreground_message), Toast.LENGTH_SHORT)

    private val enterBackgroundToast =
        Toast.makeText(context, context.getString(R.string.background_message), Toast.LENGTH_SHORT)

    /**
     * Shows foreground {@link android.widget.Toast} after attempting to cancel the background one.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        CustomLog.d("onEnterForeground", "onEnterForeground")
        //enterForegroundToast.showAfterCanceling(enterBackgroundToast)
        GlobalScope.launch(Dispatchers.IO) { FirebaseApi.updateOnlineStatus(true) }

    }

    /**
     * Shows background {@link android.widget.Toast} after attempting to cancel the foreground one.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        CustomLog.d("onEnterBackground", "onEnterBackground")
        //enterBackgroundToast.showAfterCanceling(enterForegroundToast)
        GlobalScope.launch(Dispatchers.IO) { FirebaseApi.updateOnlineStatus(false) }
    }

    private fun Toast.showAfterCanceling(toastToCancel: Toast) {
        toastToCancel.cancel()
        this.show()
    }
}