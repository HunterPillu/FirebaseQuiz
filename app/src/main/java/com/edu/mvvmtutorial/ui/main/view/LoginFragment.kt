package com.edu.mvvmtutorial.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.covidbeads.app.assesment.util.showMsg
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.base.ViewModelFactory
import com.edu.mvvmtutorial.ui.main.viewmodel.LoginViewModel
import com.edu.mvvmtutorial.utils.Status
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.login_fragment.*
import java.util.*

class LoginFragment : Fragment() {

    companion object {
        val TAG: String = "LoginFrag"

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

        viewModel.getLoginResult().observe(this, {
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
                    showMsg(context!!, R.string.unknown_error)
                }
            }
        })
    }

    private fun goNext() {
        startActivity(Intent(context, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                val user = User()

                viewModel.saveUser()

                return
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showMsg(context!!, R.string.sign_in_cancelled)
                    return
                }

                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showMsg(context!!, R.string.no_internet_connection)
                    return
                }

                if (response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    showMsg(context!!, R.string.unknown_error)
                    return
                }
            }

            showMsg(context!!, R.string.unknown_sign_in_response)

        }

    }


}