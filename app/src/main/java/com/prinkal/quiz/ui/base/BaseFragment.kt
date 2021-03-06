package com.prinkal.quiz.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.prinkal.quiz.data.model.User
import com.prinkal.quiz.ui.callbacks.FragmentEventListener


open class BaseFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    //private var TAG = BaseFragment::class.java.name
    //var layoutView: View? = null
    private lateinit var listener: FragmentEventListener
    //lateinit var connectionLiveData: ConnectionLiveData

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FragmentEventListener
        //connectionLiveData = listener.getConnectionObject()
    }

    open fun onBackPressed() {
        //Log.d(TAG, "total entry = " + activity!!.supportFragmentManager.backStackEntryCount)
        activity?.onBackPressed();
        /*if (activity!!.supportFragmentManager.backStackEntryCount > 1) {
            activity?.supportFragmentManager?.popBackStack()
        } else {
            activity?.finish();
        }*/
    }

    override fun onRefresh() {
        //to be implemented on child childFragment : Supplier, Chat fragment
    }

    fun openFragment(fragment: BaseFragment) {
        listener.openFragment(fragment)
    }

    fun updateToolbarTitle(title: String) {
        listener.updateToolbarTitle(title)
    }


    fun sendGameInvite(item: User,quizId: String) {
        listener.onInviteOpponent(item,quizId)
    }

    fun firebaseInitGame() {
        listener.firebaseInitGame()
    }

    open fun initViews() {
        //override in child fragment
    }
}
