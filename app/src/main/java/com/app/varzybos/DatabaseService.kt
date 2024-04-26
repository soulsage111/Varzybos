package com.app.varzybos

import android.content.ContentValues.TAG
import android.util.Log
import com.app.varzybos.data.Answer
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicBoolean

class DatabaseService {
    lateinit var firestore: FirebaseFirestore

    // Saves or updates event in database
    fun saveEvent(event: Event) {
        val document = if (event.eventId.isEmpty()) {
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

    fun removeEvent(event: Event) {
        val document = firestore.collection("Events").document(event.eventId)
        event.eventId = document.id

        val result = document.delete()
        result.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly removed event")
        }
        result.addOnFailureListener {
            Log.d("DatabaseService", "Failed to remove event $event")
        }
    }

    fun registerUserToEvent(eventId: String) {
        var reference = firestore.collection("Events").document(eventId)

        var document = runBlocking { reference.get().await() }
        var eventData = document.data

        val currentUser = FirebaseAuth.getInstance().currentUser


        var event: Event = Event()
        if (eventData != null) {
            event.eventName = eventData!!["eventName"].toString()
            event.eventId = eventData!!["eventId"].toString()
            event.description = eventData!!["description"].toString()
            val d = eventData!!["eventDate"] as com.google.firebase.Timestamp
            event.eventDate = d.toDate()
            event.registeredUsers = eventData!!["registeredUsers"] as ArrayList<String>
            if (eventData!!["eventTasks"] != null) {
                var tasks = ArrayList<Map<String, String>>()
                try {
                    tasks = eventData!!["eventTasks"] as ArrayList<Map<String, String>>
                } catch (e: Exception) {
                    Log.e(TAG, "Event task mapping error", e)
                }

                tasks.forEach() { task ->
                    var t = EventTask()
                    t.taskName = task["taskName"].toString()
                    t.taskDescription = task["taskDescription"].toString()
                    t.taskId = task["taskId"].toString()
                    event.eventTasks.add(t)
                }
            }
        }
        if (currentUser != null) {
            event.registeredUsers.add(currentUser.uid)
        } else {
            Log.e(TAG, "User is null")
        }


        val r = reference.set(event)
        r.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly created event")
        }
        r.addOnFailureListener {
            Log.d("DatabaseService", "Failed to create event $event")
        }
    }

    fun unregisterUserFromEvent(eventId: String) {
        var reference = firestore.collection("Events").document(eventId)

        var document = runBlocking { reference.get().await() }
        var eventData = document.data

        val currentUser = FirebaseAuth.getInstance().currentUser

        var event: Event = Event()
        if (eventData != null) {
            event.eventName = eventData!!["eventName"].toString()
            event.eventId = eventData!!["eventId"].toString()
            event.description = eventData!!["description"].toString()
            val d = eventData!!["eventDate"] as com.google.firebase.Timestamp
            event.eventDate = d.toDate()
            event.registeredUsers = eventData!!["registeredUsers"] as ArrayList<String>
            if (eventData!!["eventTasks"] != null) {
                var tasks = ArrayList<Map<String, String>>()
                try {
                    tasks = eventData!!["eventTasks"] as ArrayList<Map<String, String>>
                } catch (e: Exception) {
                    Log.e(TAG, "Event task mapping error", e)
                }

                tasks.forEach() { task ->
                    var t = EventTask()
                    t.taskName = task["taskName"].toString()
                    t.taskDescription = task["taskDescription"].toString()
                    t.taskId = task["taskId"].toString()
                    event.eventTasks.add(t)
                }
            }
        }
        if (currentUser != null) {
            event.registeredUsers.remove(currentUser.uid)
        } else {
            Log.e(TAG, "User is null")
        }


        val r = reference.set(event)
        r.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly created event")
        }
        r.addOnFailureListener {
            Log.d("DatabaseService", "Failed to create event $event")
        }
    }


    fun saveAnswer(eventId: String, taskId: String, answerId: String, answer: String) {
        var reference = firestore.collection("EventAnswers").document(answerId)

        var document = runBlocking { reference.get().await() }
        var ans = Answer()
        ans.answer = answer
        ans.taskId = taskId
        ans.eventId = eventId
        ans.answerId = answerId
        ans.userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference.set(ans)


    }


    fun initFirestore() {
        firestore = FirebaseFirestore.getInstance()
    }

    //    fun getAdminDocument(email : String) : Task<DocumentSnapshot> {
//        return firestore.collection("Admins").document(email).get().await()
//    }
    suspend fun isAdmin(email: String): Boolean {
        try {
            var value = firestore.collection("Admins").document(email).get().await()

            return value.data!!["isAdmin"] as Boolean

        } catch (e: Exception) {
            Log.w(TAG, "Not admin")
            return false
        }
        //return value.data!!["isAdmin"] as Boolean

//        value.addOnSuccessListener {
//            @Override
//            fun onSuccess(s: DocumentSnapshot) {
//                ret.set(s.data!!["isAdmin"] as Boolean)
//            }
//        }
//        return ret.get()
    }


    fun isRegisteredToEvent(eventId: String): Boolean {
        try {
            lateinit var value: DocumentSnapshot
            runBlocking {
                value = firestore.collection("Events").document(eventId).get().await()
            }
            var a = value!!["registeredUsers"] as ArrayList<String>

            return a.contains(FirebaseAuth.getInstance().currentUser?.uid.toString())


        } catch (e: Exception) {
            Log.w(TAG, "isRegisteredToEvent", e)
            return false
        }
    }
}