package com.edu.mvvmtutorial.data.api

import com.edu.mvvmtutorial.data.model.Course
import com.edu.mvvmtutorial.data.model.Quiz
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.utils.CustomLog
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.meripadhai.utils.Const
import kotlinx.coroutines.tasks.await

object FirebaseApi {
    private const val TAG = "FirebaseAPI"

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
        val ref: Query
        // courseId = -1 means fetch all data otherwise fetch filtered data

            ref =

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