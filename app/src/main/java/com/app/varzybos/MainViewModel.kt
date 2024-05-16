package com.app.varzybos

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import com.app.varzybos.data.Answer
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.data.Message
import com.app.varzybos.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.reflect.KProperty

class MainViewModel(application: Application) : AndroidViewModel(application) {
    //    var eventList: MutableLiveData<List<Event>> = MutableLiveData<List<Event>>()
    var eventList: SnapshotStateList<Event> = mutableStateListOf()
    var userList: SnapshotStateList<User> = mutableStateListOf()
    var userEventList: SnapshotStateList<Event> = mutableStateListOf()
    var currentUserList: SnapshotStateList<User> = mutableStateListOf()

    lateinit var firestore: FirebaseFirestore


    fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun getEventsToList(personal: Boolean): SnapshotStateList<Event> {
        var list: SnapshotStateList<Event> = mutableStateListOf()

        // var context = getApplication<Application>().applicationContext
        // FirebaseApp.initializeApp(context)

        try {
            var out = runBlocking { firestore.collection("Events").get().await() }
            var queryResult = out.documents
            queryResult.forEach() { doc ->
                var map = doc.data
                var event: Event = Event()
                if (map != null) {
                    event.eventName = map["eventName"].toString()
                    event.eventId = map["eventId"].toString()
                    event.description = map["description"].toString()
                    if (map["closed"] != null) {
                        event.closed = map["closed"] as Boolean
                    } else {
                        event.closed = false
                    }
                    val d = map["eventDate"] as com.google.firebase.Timestamp
                    event.eventDate = d.toDate()
                    event.registeredUsers = map["registeredUsers"] as ArrayList<String>
                    if (map!!["eventTasks"] != null) {
                        var tasks = ArrayList<Map<String, String>>()
                        try {
                            tasks = map!!["eventTasks"] as ArrayList<Map<String, String>>
                        } catch (e: Exception) {
                            Log.e(TAG, "Event task mapping error", e)
                        }

                        tasks.forEach() { task ->
                            var t = EventTask()
                            t.taskName = task["taskName"].toString()
                            t.taskDescription = task["taskDescription"].toString()

                        }
                    }
                }
                if (personal && event.registeredUsers.contains(FirebaseAuth.getInstance().currentUser?.uid.toString())) {
                    list.add(event)

                } else if (!personal) {
                    list.add(event)
                }

            }

        } catch (e: Exception) {
            Log.e(TAG, "getEventsToList exception", e)
        }
        return list
    }

    private fun getUsersToList(): SnapshotStateList<User> {
        var list: SnapshotStateList<User> = mutableStateListOf()

        try {
            var out = runBlocking { firestore.collection("UserInfo").get().await() }
            var queryResult = out.documents
            queryResult.forEach() { doc ->
                var map = doc.data
                var user = User()
                if (map != null) {
                    user.name = map["name"].toString()
                    user.surname = map["surname"].toString()
                    user.email = map["email"].toString()
                    user.id = map["id"].toString()

                }

                list.add(user)
            }

        } catch (e: Exception) {
            Log.e(TAG, "getEventsToList exception", e)
        }
        return list
    }

    fun updateEvents() {
        eventList.clear()
        userEventList.clear()
        var listas = getEventsToList(false)
        var listas2 = getEventsToList(true)
        listas.forEach { event: Event -> eventList.add(event) }
        listas2.forEach { event: Event -> userEventList.add(event) }
    }

    fun updateUsers() {
        userList.clear()
        var listas = getUsersToList()
        listas.forEach { user: User -> userList.add(user) }
    }

    fun getUsersFromList(userListForEvent: ArrayList<String>): SnapshotStateList<User> {
        var list: SnapshotStateList<User> = mutableStateListOf()

        try {
            var out = runBlocking { firestore.collection("UserInfo").get().await() }
            var queryResult = out.documents
            queryResult.forEach() { doc ->
                var map = doc.data
                var user = User()
                if (map != null) {
                    user.name = map["name"].toString()
                    user.surname = map["surname"].toString()
                    user.email = map["email"].toString()
                    user.id = map["id"].toString()

                }
                if (userListForEvent.contains(user.id)) {
                    list.add(user)
                }

            }

        } catch (e: Exception) {
            Log.e(TAG, "getUsersFromList exception", e)
        }
        return list
    }


    fun getEventFromId(id: String): Event {
        var time = System.currentTimeMillis()



        var eventData: Map<String, Any>?
        runBlocking {
            eventData =
                firestore.collection("Events").document(id).get().await().data
        }
        var event: Event = Event()
        if (eventData != null) {
            event.eventName = eventData!!["eventName"].toString()
            event.eventId = eventData!!["eventId"].toString()
            event.description = eventData!!["description"].toString()
            if (eventData!!["closed"] != null) {
                event.closed = eventData!!["closed"] as Boolean
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
        Log.e("Time getEventFromId", (System.currentTimeMillis() - time).toString())
        return event
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>): MainViewModel {
        return this
    }


    fun saveEvent(event: Event) {
        var time = System.currentTimeMillis()


        val document = if (event.eventId.isEmpty()) {
            firestore.collection("Events").document()
        } else {
            firestore.collection("Events").document(event.eventId)
        }
        event.eventId = document.id

        val result = document.set(event)
        result.addOnSuccessListener {
            Log.e("Time saveEvent", (System.currentTimeMillis() - time).toString())
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
        var time = System.currentTimeMillis()

        val result = firestore.collection("UserInfo").document(user.email).set(user)
        result.addOnSuccessListener {
            Log.e("Time saveUser", (System.currentTimeMillis() - time).toString())
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
        var time = System.currentTimeMillis()
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
            Log.e("Time registerUserToEvent", (System.currentTimeMillis() - time).toString())
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
        var time = System.currentTimeMillis()
        var msg = Message()
        msg.message = message
        msg.timestamp = System.currentTimeMillis()
        msg.id = UUID.randomUUID().toString()
        msg.from = FirebaseAuth.getInstance().currentUser?.uid.toString()
        msg.to = to
        val result = firestore.collection("Messages").document(msg.id).set(msg)
        result.addOnSuccessListener {
            Log.e("Time sendMessage", (System.currentTimeMillis() - time).toString())
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