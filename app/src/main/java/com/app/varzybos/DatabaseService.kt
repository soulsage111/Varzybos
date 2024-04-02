package com.app.varzybos

import android.util.Log
import com.app.varzybos.data.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

class DatabaseService {
    lateinit var firestore : FirebaseFirestore

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

    fun initFirestore(){
        firestore = FirebaseFirestore.getInstance()
    }

    fun isAdmin(email : String): Boolean{
        var a = AtomicBoolean(false)
        var value = firestore.collection("Admins").document(email).get().addOnSuccessListener {
            @Override
            fun onSuccess(){
                a.set(true)
            }

        }
        return if(a.get()){
            value.result.data!!["isAdmin"] as Boolean

        } else {
            true
        }
    }

}