package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.ui.base.BaseActivity
import com.edu.mvvmtutorial.ui.main.viewmodel.HomeActivityViewModel
import com.edu.mvvmtutorial.utils.CustomLog
import com.edu.mvvmtutorial.utils.Status

class HomeActivity : BaseActivity() {
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pq_activity_home)
        setupViewModel()
        setupDataObserver()
        /*if (savedInstanceState == null) {
            CustomLog.e(TAG, "savedInstanceState")
            openRequiredFragment()
        }*/
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
    }

    private fun setupDataObserver() {
        viewModel.getData().observe(this, {

            CustomLog.e(TAG, "data observer ${it.status.name}")
            when (it.status) {
                Status.IDLE -> {
                    initParent()
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