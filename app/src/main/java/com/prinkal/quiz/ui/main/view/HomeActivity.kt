package com.prinkal.quiz.ui.main.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.prinkal.quiz.R
import com.prinkal.quiz.ui.base.BaseActivity
import com.prinkal.quiz.ui.main.viewmodel.HomeActivityViewModel
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Status

class HomeActivity : BaseActivity() {
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pq_activity_home)
        setupViewModel()
        setupDataObserver()
        setupInvitationObserver()
        /*if (savedInstanceState == null) {
            CustomLog.e(TAG, "savedInstanceState")
            openRequiredFragment()
        }*/
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