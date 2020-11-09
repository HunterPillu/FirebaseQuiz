package com.prinkal.quiz.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.prinkal.quiz.BuildConfig
import com.prinkal.quiz.R
import com.prinkal.quiz.data.api.FirebaseApi
import com.prinkal.quiz.data.model.Quizes
import com.prinkal.quiz.data.model.Reference
import com.prinkal.quiz.utils.Connectivity
import com.prinkal.quiz.utils.Const
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.shortToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import kotlinx.android.synthetic.main.pq_activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class SplashActivity : AppCompatActivity() {
    var isFinished = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pq_activity_splash)
        tvVersion.setText(BuildConfig.VERSION_NAME)
        fetchReferenceData()
        bUpload.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                isFinished = true
                savedDummyData()
            }
        }
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

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val iss: InputStream = getAssets().open("quiestion_list.json")
            val size: Int = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private suspend fun savedDummyData() {
        try {
            //val obj = JSONObject(loadJSONFromAsset()!!)
            val courses = Gson().fromJson(loadJSONFromAsset(), Quizes::class.java)
            //val questions=Gson().fromJson(loadJSONFromAsset(),Courses::class.java)
            val isSuccess = FirebaseApi.uploadCourses(courses)
            if (isSuccess) {
                openNextScreen()
            } else {
                CustomLog.e("error", "something went wrong")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}