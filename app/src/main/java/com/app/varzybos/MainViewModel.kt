package com.app.varzybos

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.data.User
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.reflect.KProperty

class MainViewModel(application: Application) : AndroidViewModel(application) {
//    var eventList: MutableLiveData<List<Event>> = MutableLiveData<List<Event>>()
    var eventList : SnapshotStateList<Event> = mutableStateListOf()
    var databaseService: DatabaseService = DatabaseService()
    var user : User = User()

    fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun checkIfAdmin() : Boolean {
        var r:Boolean
        runBlocking { r = databaseService.isAdmin(user.email) }
        return r
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun getEventsToList(): SnapshotStateList<Event> {
        var list : SnapshotStateList<Event> = mutableStateListOf()

        // var context = getApplication<Application>().applicationContext
        // FirebaseApp.initializeApp(context)

        try {
            var out = runBlocking { databaseService.firestore.collection("Events").get().await() }
            var queryResult = out.documents
            queryResult.forEach(){doc ->
                var map = doc.data
                var event : Event = Event()
                if (map != null) {
                    event.eventName = map["eventName"].toString()
                    event.eventId = map["eventId"].toString()
                    event.description = map["description"].toString()
                    val d = map["eventDate"] as com.google.firebase.Timestamp
                    event.eventDate = d.toDate()
                    event.registeredUsers = map["registeredUsers"] as List<String>
                    if (map!!["eventTasks"]!=null){
                        (map!!["eventTasks"] as List<HashMap<String, String>>).forEach(){task ->
                            var t = EventTask()
                            t.taskName = task["taskName"].toString()
                            t.taskDescription = task["taskDescription"].toString()

                        }
                    }
                }
                list.add(event)
            }

        }catch (e:Exception){
            Log.e(TAG, "getEventsToList exception", e)
        }
        return list
    }

    fun getListFromSnapshot(snapshot: QuerySnapshot){

    }

    fun updateEvents(){
        eventList.clear()
        var listas = getEventsToList()
        listas.forEach { event: Event ->  eventList.add(event)}
    }

     fun getEventFromId(id: String): Event{
         var eventData: Map<String, Any>?
         runBlocking {
             eventData = databaseService.firestore.collection("Events").document(id).get().await().data
         }
         var event: Event = Event()
         if (eventData != null){
             event.eventName = eventData!!["eventName"].toString()
             event.eventId = eventData!!["eventId"].toString()
             event.description = eventData!!["description"].toString()
             val d = eventData!!["eventDate"] as com.google.firebase.Timestamp
             event.eventDate = d.toDate()
             event.registeredUsers = eventData!!["registeredUsers"] as List<String>
             event.eventTasks = eventData!!["eventTasks"] as List<EventTask>
         }
        return event
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>): MainViewModel {
        return this
    }

}