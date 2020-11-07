package com.edu.mvvmtutorial.data.api

import com.edu.mvvmtutorial.data.model.*
import com.edu.mvvmtutorial.utils.Const
import com.edu.mvvmtutorial.utils.CustomLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirebaseApi {
    private const val TAG = "FirebaseAPI"

    //create update user on firebase
    suspend fun uploadCourses(data: Quizes): Boolean {
        val courseRef = Firebase.firestore
            .collection(Const.TABLE_QUIZ)

        return try {
            for (quiz in data.quizes!!) {


                val questions = arrayListOf<Question>()
                questions.addAll(quiz.questions!!)
                quiz.questions = null

                val uid = courseRef.document().id
                quiz.uid = uid
                courseRef.document(uid).set(quiz)
                    .await()

                for (ques in questions) {
                    val quesRef = courseRef.document(uid).collection(Const.TABLE_QUESTION)
                    val quesId = quesRef.document().id
                    ques.uid = quesId
                    quesRef.document(quesId).set(ques).await()
                }
            }
            true
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            false
        }
    }

    //create update user on firebase
    suspend fun createUser(user: User): Boolean {
        return try {
            Firebase.firestore
                .collection(Const.TABLE_USERS)
                .document(user.uid)
                .set(user, SetOptions.merge())

                .await()
            true
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            false
        }
    }

    suspend fun updateFirebaseToken() {
        updateUserField(
            hashMapOf<String, Any?>().apply {
                this["firebaseToken"] =
                    FirebaseAuth.getInstance().currentUser!!.getIdToken(true).await().token
            }
        )
    }

    // update generic data of any firebase
    suspend fun updateDbValue(map: Map<String, Any?>, ref: DocumentReference) {
        try {
            //ref.update(field, value).await()
            ref.set(map, SetOptions.merge()).await()
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
        }
    }


    // update presence of user firebase
    suspend fun updateUserField(map: Map<String, Any?>) {
        updateDbValue(
            map,
            Firebase.firestore
                .collection(Const.TABLE_USERS)
                .document(Firebase.auth.currentUser?.uid!!)
        )
    }


    // update presence of user firebase
    suspend fun updateOnlineStatus(online: Boolean) {
        if (null != Firebase.auth.currentUser) {
            val map = hashMapOf<String, Any?>()
            map["online"] = online
            updateUserField(map)
        }
    }

    //fetch quiz list as per courseID
    suspend fun fetchQuiz(courseId: Int): List<Quiz> {
        val ref: Query
        // courseId = -1 means fetch all data otherwise fetch filtered data
        if (courseId > -1) {
            ref = Firebase.firestore
                .collection(Const.TABLE_QUIZ)
                .whereEqualTo("courseId", courseId)//.limit(Const.LIMIT)
        } else {
            ref = Firebase.firestore
                .collection(Const.TABLE_QUIZ)
        }
        return try {
            val data = ref.get()
                .await()
            data.toObjects()
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            ArrayList()
        }
    }

    suspend fun getQuizWithQuestion(quizId: String): Quiz {
        return mergeQuizAndQuestion(fetchQuiz(quizId), fetchQuestion(quizId))
    }

    private fun mergeQuizAndQuestion(quiz: Quiz, ques: List<Question>): Quiz {
        quiz.questions = ques
        return quiz
    }

    //fetch players list excluding self
    suspend fun fetchPlayers(): List<User> {

        val quizRef = Firebase.firestore
            .collection(Const.TABLE_USERS)
            .whereNotEqualTo("uid", Firebase.auth.currentUser?.uid)

        return try {
            val data = quizRef.get().await()
            data.toObjects()
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            ArrayList()
        }
    }

    //fetch quiz question list as per quizId
    suspend fun fetchQuestion(quizId: String): List<Question> {

        val quizRef = Firebase.firestore
            .collection(Const.TABLE_QUIZ)
            .document(quizId).collection(Const.TABLE_QUESTION).limit(10)

        return try {
            val data = quizRef.get().await()
            data.toObjects()
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            ArrayList()
        }
    }


    //fetch quiz question list as per quizId
    suspend fun fetchQuiz(quizId: String): Quiz {

        val quizRef = Firebase.firestore
            .collection(Const.TABLE_QUIZ)
            .document(quizId)

        return try {
            val data = quizRef.get().await()
            data.toObject()!!
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            Quiz()
        }
    }

    //fetch all courses
    suspend fun fetchCourse(): List<Course> {
        // courseId = -1 means fetch all data otherwise fetch filtered data
        return try {
            val data = Firebase.firestore
                .collection(Const.TABLE_COURSE).get()
                .await()
            data.toObjects()
        } catch (e: Exception) {
            CustomLog.e(TAG, e)
            ArrayList()
        }
    }

    /*suspend fun createRoom(uid: String) {
        Firebase.firestore
            .collection(Const.TABLE_ROOM).document(uid).set(GameRoom())
            .await()
    }*/

}