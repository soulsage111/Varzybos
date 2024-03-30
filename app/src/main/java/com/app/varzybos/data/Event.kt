package com.app.varzybos.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class Event(){
    lateinit var eventId: String
    var eventName: String = ""
    var description: String = ""
    var eventDate: Date = Date()
    var registeredUsers: List<String> = listOf()

    class Event(eventName: String, description: String, eventDate: Date){

    }
}
