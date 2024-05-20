package com.app.varzybos.data

import java.util.Date

class Event {
    lateinit var eventId: String
    var eventName: String = ""
    var description: String = ""
    var eventDate: Date = Date()
    var registeredUsers: ArrayList<String> = arrayListOf()
    var eventTasks: ArrayList<EventTask> = arrayListOf()
    var closed: Boolean = false
    //var thumbnail : ImageBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565).asImageBitmap()

    class Event(eventName: String, description: String, eventDate: Date)
}
