package com.edu.mvvmtutorial.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.covidbeads.app.assesment.util.shortToast
import com.edu.mvvmtutorial.BuildConfig
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.Reference
import com.edu.mvvmtutorial.utils.CustomLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.meripadhai.utils.Connectivity
import com.meripadhai.utils.Const
import kotlinx.android.synthetic.main.pq_activity_splash.*

class SplashActivity : AppCompatActivity() {
    var isFinished = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pq_activity_splash)
        tvVersion.setText(BuildConfig.VERSION_NAME)
        fetchReferenceData()

    }

    private fun fetchReferenceData() {
        if (!Connectivity.isConnected(this)) {
            shortToast(this, R.string.no_internet_connection)
            onBackPressed()
            return
        }
        FirebaseFirestore.getInstance().collection(Const.TABLE_REFERENCE)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    for (postSnapshot in documents) {
                        CustomLog.d("splash", postSnapshot.toString())
                        val ref = postSnapshot.toObject<Reference>()
                        if (ref.appVersion > BuildConfig.VERSION_CODE) {
                            showUpgradeDialog(ref)
                        } else if (ref.error) {
                            showErrorDialog(ref)
                        } else {
                            openNextScreen()
                        }
                        return@addOnSuccessListener
                    }
                    if (documents.isEmpty) {
                        openNextScreen()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .addOnFailureListener { exception ->
                //onBackPressed()
                CustomLog.e(Const.FB_ERROR, exception.localizedMessage)
                openNextScreen()
            }
    }

    private fun showErrorDialog(errorData: Reference) {
        //todo show app error dialog
        /* val bottomSheetFragment = ErrorDialog.newInstance(errorData)
         bottomSheetFragment.setCancelable(false)
         bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)*/
    }

    private fun showUpgradeDialog(forceUpdate: Reference) {
        //todo show app upgrade dialog
        /*val bottomSheetFragment = UpgradeDialog.newInstance(forceUpdate)
        bottomSheetFragment.setCancelable(false)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)*/
    }

    override fun onBackPressed() {
        isFinished = true
        super.onBackPressed()
    }

    private fun openNextScreen() {
        Handler().postDelayed({
            // This method will be executed once the timer is over
            if (!isFinished) {
                val intent: Intent
                intent = Intent(this@SplashActivity, HomeActivity::class.java)
                if (FirebaseAuth.getInstance().currentUser != null) {
                    intent.putExtra("screen", "home")
                } else {
                    intent.putExtra("screen", "login")
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
        }, 1000)
    }
}