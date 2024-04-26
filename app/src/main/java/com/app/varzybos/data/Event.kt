package com.app.varzybos.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class Event(){
    lateinit var eventId: String
    var eventName: String = ""
    var description: String = ""
    var eventDate: Date = Date()
    var registeredUsers: ArrayList<String> = arrayListOf()
    var eventTasks: ArrayList<EventTask> = arrayListOf()
    //var thumbnail : ImageBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565).asImageBitmap()

    class Event(eventName: String, description: String, eventDate: Date){

    }
}
