package com.app.varzybos.chat

data class Message(
    var id: String = "",
    var from: String = "",
    var to: String = "",
    var message: String = "",
    var timestamp: Long = 0
)