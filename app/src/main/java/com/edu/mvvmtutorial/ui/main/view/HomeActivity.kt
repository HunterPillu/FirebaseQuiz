package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.edu.mvvmtutorial.R

class HomeActivity : AppCompatActivity() {
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
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, HomeFragment.newInstance(), HomeFragment.TAG)
                    .commit()
            }
            "login" -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance(), LoginFragment.TAG)
                    .commit()
            }
        }
    }
}