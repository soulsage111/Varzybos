package com.app.varzybos

import android.content.ContentValues.TAG
import android.util.Log
import com.app.varzybos.chat.Message
import com.app.varzybos.data.Answer
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.data.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.UUID
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

    fun saveUser(user: User) {

        val result = firestore.collection("UserInfo").document(user.email).set(user)
        result.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly created user")
        }
        result.addOnFailureListener {
            Log.d("DatabaseService", "Failed to create user")
        }
    }

    fun updateUserUid(user: User) {

        val result = firestore.collection("UserInfo").document(user.email).set(user)
        result.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly created user")
        }
        result.addOnFailureListener {
            Log.d("DatabaseService", "Failed to create user")
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
            event.closed = eventData!!["closed"] as Boolean
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
            if (eventData["closed"] != null) {
                event.closed = eventData["closed"] as Boolean
            } else {
                event.closed = false
            }
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

    fun stoppedEvent(eventId: String, value: Boolean) {
        var reference = firestore.collection("Events").document(eventId)

        var document = runBlocking { reference.get().await() }
        var eventData = document.data

        val currentUser = FirebaseAuth.getInstance().currentUser


        var event: Event = Event()
        if (eventData != null) {
            event.eventName = eventData!!["eventName"].toString()
            event.eventId = eventData!!["eventId"].toString()
            event.description = eventData!!["description"].toString()
            event.closed = value
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
        ans.score = 0
        ans.userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference.set(ans)
    }

    fun saveAnswerList(eventId: String, taskId: String, answerId: String, answer: String) {
        var reference = firestore.collection("EventAnswers").document(answerId)

        var document = runBlocking { reference.get().await() }
        var ans = Answer()
        ans.answer = answer
        ans.taskId = taskId
        ans.eventId = eventId
        ans.answerId = answerId
        ans.score = 0
        ans.userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference.set(ans)


    }


    fun scoreAnswer(eventId: String, taskId: String, answerId: String, score: Long) {
        var reference = firestore.collection("EventAnswers").document(answerId)

        var document = runBlocking { reference.get().await() }
        var data = document.data

        var ans = Answer()
        ans.answer = data?.get("answer").toString()
        ans.taskId = data?.get("taskId").toString()
        ans.eventId = data?.get("eventId").toString()
        ans.answerId = data?.get("answerId").toString()
        ans.score = score
        ans.userId = data?.get("userId").toString()

        reference.set(ans)

    }


    fun getAnswersForUser(eventId: String, userId: String): ArrayList<Answer> {
        var result = ArrayList<Answer>()
        var reference = firestore.collection("EventAnswers")
        var a: QuerySnapshot
        runBlocking {
            a = reference.get().await()
        }
        a.forEach { it ->
            var data = it.data
            if (data["eventId"] == eventId) {
                if (data["userId"] == userId) {
                    var ans = Answer()
                    ans.answer = data["answer"].toString()
                    ans.taskId = data["taskId"].toString()
                    ans.eventId = data["eventId"].toString()
                    ans.answerId = data["answerId"].toString()
                    ans.userId = data["userId"].toString()
                    if (data["score"] != null) {
                        ans.score = data["score"]!! as Long
                    } else {
                        ans.score = 0
                    }

                    result.add(ans)
                }
            }
        }

        return result
    }

    fun getAnswer(answerId: String): Answer {
        var reference = firestore.collection("EventAnswers").document(answerId)
        var a: DocumentSnapshot
        runBlocking {
            a = reference.get().await()
        }
        var ans = Answer()
        var data = a.data
        if (data != null) {
            ans.answer = data["answer"].toString()
            ans.taskId = data["taskId"].toString()
            ans.eventId = data["eventId"].toString()
            ans.answerId = data["answerId"].toString()
            ans.score = data["score"]!! as Long
            ans.userId = data["userId"].toString()
        }

        return ans
    }


    fun getScores(eventId: String): Map<String, Long> {
        var result: MutableMap<String, Long> = mutableMapOf()
        var reference = firestore.collection("EventAnswers")
        var a: QuerySnapshot
        runBlocking {
            a = reference.get().await()
        }
        a.forEach { it ->
            var data = it.data
            if (data["eventId"] == eventId) {
                var currentScore = 0L
                if (result[data["userId"].toString()] != null){
                    currentScore = result[data["userId"].toString()]!!
                    result[data["userId"].toString()] = currentScore + data["score"]!! as Long
                } else {
                    if (data["score"] != null){
                        result[data["userId"].toString()] = (data["score"]!! as Long)
                    }
                }

            }
        }
        return result
    }

    fun sendMessage(message: String, to: String) {
        var msg = Message()
        msg.message = message
        msg.timestamp = System.currentTimeMillis()
        msg.id = UUID.randomUUID().toString()
        msg.from = FirebaseAuth.getInstance().currentUser?.uid.toString()
        msg.to = to
        val result = firestore.collection("Messages").document(msg.id).set(msg)
        result.addOnSuccessListener {
            Log.d("DatabaseService", "Successfuly sent message")
        }
        result.addOnFailureListener {
            Log.d("DatabaseService", "Failed to send message")
        }
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

    fun isEventStopped(eventId: String): Boolean {
        try {
            lateinit var value: DocumentSnapshot
            runBlocking {
                value = firestore.collection("Events").document(eventId).get().await()
            }
            var a = value!!["closed"] as Boolean

            return a


        } catch (e: Exception) {
            Log.w(TAG, "isEventStopped", e)
            return false
        }
    }

}