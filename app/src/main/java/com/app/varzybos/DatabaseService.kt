package com.app.varzybos

import android.util.Log
import com.app.varzybos.data.Event
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicBoolean

class DatabaseService {
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    // Saves or updates event in database
    fun saveEvent(event: Event){
        val document = if (event.eventId == null || event.eventId.isEmpty()){
            firestore.collection("Events").document()
        } else {
            firestore.collection("Events").document(event.eventId)
        }
        event.eventId = document.id

        val result = document.set(event)
        result.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly created event")
        }
        result.addOnFailureListener {
            Log.d("DatabaseService", "Failed to create event $event")
        }
    }

//    fun getAdminDocument(email : String) : Task<DocumentSnapshot> {
//        return firestore.collection("Admins").document(email).get().await()
//    }
    fun isAdmin(email : String): Boolean{
        var value = firestore.collection("Admins").document(email).get()
        var ret = AtomicBoolean(false)

        //return value.data!!["isAdmin"] as Boolean

        value.addOnSuccessListener {
            @Override
            fun onSuccess(s: DocumentSnapshot) {
                ret.set(s.data!!["isAdmin"] as Boolean)
            }
        }
        return ret.get()
    }
}