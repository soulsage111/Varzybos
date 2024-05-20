package com.app.varzybos.data

import org.osmdroid.util.GeoPoint

data class EventTask(
    var taskId: String = "",
    var taskName: String = "",
    var taskDescription: String = "",
    var route: ArrayList<GeoPoint> = arrayListOf()
)
