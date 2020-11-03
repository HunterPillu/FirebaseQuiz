package com.edu.mvvmtutorial.ui.main.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.ui.base.ViewModelFactory
import com.edu.mvvmtutorial.ui.main.viewmodel.LoginViewModel
import com.edu.mvvmtutorial.ui.main.viewmodel.MainViewModel

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        ViewModelProviders.of(
            this,
            ViewModelFactory()
        ).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}