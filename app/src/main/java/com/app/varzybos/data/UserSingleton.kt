package com.app.varzybos.data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.app.varzybos.DatabaseService
import com.google.firebase.auth.FirebaseUser

object UserSingleton {
    var firebaseUser: FirebaseUser? = null
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var email by mutableStateOf("")
    var token by mutableStateOf("")

    fun initialize(firebaseUser: FirebaseUser) {
        email = firebaseUser.email.toString()
        this.firebaseUser = firebaseUser
        var d = DatabaseService()
        d.initFirestore()

        var databaseReference = d.firestore.collection("UserInfo").document(firebaseUser.uid).get()
        databaseReference.addOnSuccessListener { it ->
                name = it["name"].toString()
                surname = it["surname"].toString()
            }.addOnFailureListener { error ->
            Log.e(TAG,"User init failed", error)
        }
    }
}