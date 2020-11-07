package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.ui.base.BaseActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pq_activity_home)
        openRequiredFragment()
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
        }
    }
}