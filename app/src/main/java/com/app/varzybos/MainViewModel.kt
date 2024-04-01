package com.app.varzybos

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.varzybos.data.Event
import com.app.varzybos.data.User
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import java.util.Date

class MainViewModel(application: Application): AndroidViewModel(application) {
//    var eventList: MutableLiveData<List<Event>> = MutableLiveData<List<Event>>()
    var eventList: List<Event> = listOf()
    var databaseService: DatabaseService = DatabaseService()
    var user : User = User()

    init {
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

    fun checkIfAdmin() : Boolean {
        return databaseService.isAdmin(user.email)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun getEventsToList(): List<Event> {
        var eventList : List<Event> = listOf()
        var event : Event = Event()
        var context = getApplication<Application>().applicationContext
        FirebaseApp.initializeApp(context)
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

}