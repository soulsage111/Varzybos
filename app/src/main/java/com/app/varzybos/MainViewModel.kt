package com.app.varzybos

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.data.User
import com.app.varzybos.data.UserSingleton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.reflect.KProperty

class MainViewModel(application: Application) : AndroidViewModel(application) {
    //    var eventList: MutableLiveData<List<Event>> = MutableLiveData<List<Event>>()
    var eventList: SnapshotStateList<Event> = mutableStateListOf()
    var userList: SnapshotStateList<User> = mutableStateListOf()
    var userEventList: SnapshotStateList<Event> = mutableStateListOf()
    var databaseService: DatabaseService = DatabaseService()

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
            var out = runBlocking { databaseService.firestore.collection("Events").get().await() }
            var queryResult = out.documents
            queryResult.forEach() { doc ->
                var map = doc.data
                var event: Event = Event()
                if (map != null) {
                    event.eventName = map["eventName"].toString()
                    event.eventId = map["eventId"].toString()
                    event.description = map["description"].toString()
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
            var out = runBlocking { databaseService.firestore.collection("UserInfo").get().await() }
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

    fun getEventFromId(id: String): Event {
        var eventData: Map<String, Any>?
        runBlocking {
            eventData =
                databaseService.firestore.collection("Events").document(id).get().await().data
        }
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
        return event
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>): MainViewModel {
        return this
    }

}