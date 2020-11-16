package com.prinkal.quiz.ui.main.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.prinkal.quiz.R
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.ui.base.BaseActivity
import com.prinkal.quiz.ui.firebase.FirebaseData
import com.prinkal.quiz.ui.main.viewmodel.HomeActivityViewModel
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.PrefUtil
import com.prinkal.quiz.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : BaseActivity() {
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pq_activity_home)
        setupViewModel()
        setupDataObserver()
        setupInvitationObserver()
        updateFcmToken()
    }


    /*NEED:
    to update FCM token and save it sharepreference ,context must be passed to other class
    and I dont want that thats why data-part is handled on view instead of viewmodel*/
    private fun updateFcmToken() {
        GlobalScope.launch(Dispatchers.IO) {
            val oldToken = PrefUtil.getToken(this@HomeActivity)
            val newToken = FirebaseApi.getFcmToken()
            if (oldToken != newToken && newToken != null) {
                val map = hashMapOf<String, Any?>()
                map["firebaseToken"] = newToken
                FirebaseApi.updateUserFieldById(FirebaseData.myID, map)
                PrefUtil.saveToken(this@HomeActivity, newToken)
            }
        }
    }

    override fun fetchViewModel(): HomeActivityViewModel = viewModel

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
    }

    private fun setupDataObserver() {
        viewModel.getData().observe(this, {

            CustomLog.e(TAG, "data observer ${it.status.name}")
            when (it.status) {
                Status.IDLE -> {
                    openRequiredFragment()
                }
                else -> {
                }
            }
        })
    }

    private fun openRequiredFragment() {
        val destScreen = intent.getStringExtra("screen")
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        when (destScreen) {
            "home" -> {
                openFragment(HomeFragment.newInstance())
            }
            "login" -> {
                openFragment(LoginFragment.newInstance())
            }
            else -> {
                CustomLog.d(Companion.TAG, "bundle data not available")
            }
        }
    }

    companion object {
        internal val TAG = HomeActivity::class.java.name
    }
}