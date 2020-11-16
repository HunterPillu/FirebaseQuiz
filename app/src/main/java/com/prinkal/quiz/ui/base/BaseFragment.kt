package com.prinkal.quiz.ui.base

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.callbacks.FragmentEventListener
import com.prinkal.quiz.utils.CustomLog


open class BaseFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var TAG = BaseFragment::class.java.name
    private lateinit var listener: FragmentEventListener
    //lateinit var connectionLiveData: ConnectionLiveData

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FragmentEventListener
        //connectionLiveData = listener.getConnectionObject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, onBackPressedCallback)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            CustomLog.d(TAG, "parent handleOnBackPressed invoked")
            onBackPressed()
        }
    }

    fun removedOnBackCallback() {
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
    }

    //override this method on child fragment to intercept onBackPressed()
    open fun onBackPressed() {
        // if you want onBackPressed() to be called as normal afterwards
        if (onBackPressedCallback.isEnabled) {
            CustomLog.d(TAG, "parent onBackPressed invoked")
            removedOnBackCallback()
            listener.onCustomBackPressed();
        }

    }

    override fun onRefresh() {
        //to be implemented on child childFragment : Supplier, Chat fragment
    }

    fun openFragment(fragment: BaseFragment) {
        listener.openFragment(fragment)
    }

    fun sendGameInvite(item: User, quizId: String) {
        listener.onInviteOpponent(item, quizId)
    }

    fun firebaseInitGame() {
        listener.firebaseInitGame()
    }

    open fun initViews() {
        //override in child fragment
    }


}
