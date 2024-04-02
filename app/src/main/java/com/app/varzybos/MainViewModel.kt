package com.app.varzybos

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.app.varzybos.data.Event
import com.app.varzybos.data.User
import java.util.Date
import kotlin.reflect.KProperty

class MainViewModel() : ViewModel() {
//    var eventList: MutableLiveData<List<Event>> = MutableLiveData<List<Event>>()
    var eventList: List<Event> = listOf()
    var databaseService: DatabaseService = DatabaseService()
    var user : User = User()

    init {

    }

    fun checkIfAdmin() : Boolean {
        return databaseService.isAdmin(user.email)
    }

    fun getEventsToList(): List<Event> {
        var eventList : List<Event> = listOf()
        var event : Event = Event()
        var queryResult = databaseService.firestore.collection("Events").get().result.documents
        queryResult.forEach(){doc ->
            var map = doc.data
            if (map != null) {
                event.eventName = map["eventName"].toString()
                event.eventId = map["eventId"].toString()
                event.description = map["description"].toString()
                event.eventDate = map["eventDate"] as Date
                event.registeredUsers = map["registeredUsers"] as List<String>
            }
            eventList = eventList + event
        }
        return eventList
    }

    fun startEventListening(){
        databaseService.firestore.collection("Events").document().addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen to events failed; ", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                getEventsToList()
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