package com.edu.mvvmtutorial.data.api

import com.edu.mvvmtutorial.data.model.*
import com.edu.mvvmtutorial.utils.Const
import com.edu.mvvmtutorial.utils.CustomLog
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
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
                quiz.uid=uid
                courseRef.document(uid).set(quiz)
                    .await()

                for (ques in questions) {
                    val quesRef = courseRef.document(uid).collection(Const.TABLE_QUESTION)
                    val quesId = quesRef.document().id
                    ques.uid=quesId
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

}