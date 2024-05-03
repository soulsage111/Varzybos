package com.app.varzybos

import android.content.ContentValues.TAG
import android.util.Log
import com.app.varzybos.data.Message
import com.app.varzybos.data.Answer
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.UUID

class DatabaseService {
    lateinit var firestore: FirebaseFirestore

    // Saves or updates event in database


}