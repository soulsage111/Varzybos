package com.app.varzybos.data

data class Message(
    var id: String = "",
    var from: String = "",
    var to: String = "",
    var message: String = "",
    var timestamp: Long = 0
)