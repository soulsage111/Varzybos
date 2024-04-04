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
import com.app.varzybos.data.User
import com.google.firebase.database.ServerValue
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
    private fun getEventsToList(): List<Event> {
        var eventList : List<Event> = listOf()
        var event : Event = Event()
        // var context = getApplication<Application>().applicationContext
        // FirebaseApp.initializeApp(context)

        try {
            var out = runBlocking { databaseService.firestore.collection("Events").get().await() }
            var queryResult = out.documents
            queryResult.forEach(){doc ->
                var map = doc.data
                if (map != null) {
                    event.eventName = map["eventName"].toString()
                    event.eventId = map["eventId"].toString()
                    event.description = map["description"].toString()
                    val d = map["eventDate"] as com.google.firebase.Timestamp
                    val dateResult = d.toDate()
                    event.eventDate = dateResult
                    event.registeredUsers = map["registeredUsers"] as List<String>
                }
                eventList = eventList + event
            }

        }catch (e:Exception){
            Log.e(TAG, "getEventsToList exception", e)
        }
        return eventList
    }

    fun getListFromSnapshot(snapshot: QuerySnapshot){

    }


    fun startEventListening(){
        eventList.clear()
        var listas = getEventsToList()
        listas.forEach { event: Event ->  eventList.add(event)}
        databaseService.firestore.collection("Events").document().addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen to events failed; ", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                eventList.clear()
                getEventsToList().forEach { event: Event ->  eventList.add(event)}
                Log.d(ContentValues.TAG, "Event data updated")
            } else {
                Log.d(ContentValues.TAG, "Event data empty")
            }
        }
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>): MainViewModel {
        return this
    }

}