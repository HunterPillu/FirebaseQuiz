package com.prinkal.quiz.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.prinkal.quiz.R
import com.prinkal.quiz.ui.base.BaseFragment
import com.prinkal.quiz.ui.main.viewmodel.LoginViewModel
import com.prinkal.quiz.utils.Status
import com.prinkal.quiz.utils.showMsg
import kotlinx.android.synthetic.main.pq_fragment_login.*
import java.util.*

class LoginFragment : BaseFragment() {

    companion object {
        val TAG: String = "LoginFrag"

        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pq_fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setupObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bLogin.setOnClickListener(logInClick)

    }

    val logInClick = View.OnClickListener {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.logo)
                .setIsSmartLockEnabled(false /* credentials */, true /* hints */)
                .setAvailableProviders(
                    Arrays.asList(
                        //AuthUI.IdpConfig.PhoneBuilder().build()
                        AuthUI.IdpConfig.EmailBuilder().build()
                        //, AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                ).build(),
            123
        )
    }

    private fun setupObserver() {

        viewModel.getLoginResult().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.IDLE -> {
                    progressBar.visibility = View.GONE
                    bLogin.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    bLogin.visibility = View.VISIBLE
                    goNext()
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    bLogin.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    showMsg(requireContext(), R.string.unknown_error)
                }
            }
        })
    }

    private fun goNext() {
        firebaseInitGame()
        activity?.supportFragmentManager?.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        );
        openFragment(HomeFragment.newInstance())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                viewModel.saveUser()
                return
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showMsg(requireContext(), R.string.sign_in_cancelled)
                    return
                }

                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showMsg(requireContext(), R.string.no_internet_connection)
                    return
                }

                if (response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    showMsg(requireContext(), R.string.unknown_error)
                    return
                }
            }

            showMsg(requireContext(), R.string.unknown_sign_in_response)

        }

    }


}