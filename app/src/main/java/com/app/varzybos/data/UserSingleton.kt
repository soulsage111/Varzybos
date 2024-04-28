package com.app.varzybos.data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.varzybos.DatabaseService
import com.app.varzybos.chat.Chat
import com.app.varzybos.chat.Message
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object UserSingleton {
    var firebaseUser: FirebaseUser? = null
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var email by mutableStateOf("")
    var token by mutableStateOf("")
    //var messages = mutableListOf(Chat())
    val messages: MutableLiveData<ArrayList<Message>> by lazy { MutableLiveData<ArrayList<Message>>() }

    fun initialize(firebaseUser: FirebaseUser) {
        email = firebaseUser.email.toString()
        this.firebaseUser = firebaseUser
        val d = DatabaseService()
        d.initFirestore()
        val databaseReference: DocumentSnapshot
        runBlocking {
            databaseReference = d.firestore.collection("UserInfo").document(email).get().await()
        }
        name = databaseReference["name"].toString()
        surname = databaseReference["surname"].toString()

//        d.firestore.collection("Chat").addSnapshotListener { value, error ->
//
//            var list = ArrayList<Chat>()
//            if (value != null) {
//                value.documents.forEach { it ->
//                     var data = it.data
//                    if (data != null) {
//                        if(data.values.contains(firebaseUser.uid)){
//                            var ch = Chat()
//                            ch.id = data["id"]
//                            ch.participant1 = data["participant1"]
//                            ch.participant2 = data["participant2"]
//                            ch.messageList = data["participant2"] as ArrayList<String>
//
//
//                            list.add()
//                        }
//                    }
//                }
//            }
//        }


        d.firestore.collection("Messages").addSnapshotListener { value, error ->

            var list = ArrayList<Message>()
            value?.documents?.forEach { it ->
                var data = it.data
                if (data != null) {
                    if(data.values.contains(firebaseUser.uid)){
                        var ch = Message()
                        ch.id = data["id"].toString()
                        ch.from = data["from"].toString()
                        ch.to = data["to"].toString()
                        ch.message = data["message"].toString()
                        ch.timestamp = data["timestamp"] as Long

                        list.add(ch)
                    }
                }
            }
            messages.value = ArrayList(list.sortedByDescending { it.timestamp })


        }

    }
}

interface SingletonListener {
    fun onSingletonChanged(newValue: Any)
}