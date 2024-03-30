package com.app.varzybos

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.firebase.FirebaseApp

@ExperimentalMaterial3Api

class MainActivity : Application() {
    //var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        FirebaseApp.initializeApp(applicationContext)
        //auth = Firebase.auth

//        var intent = Intent(this , MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
//        startActivity(intent)
    }


    companion object {
        lateinit  var appContext: Context
        var mainViewModel: MainViewModel = MainViewModel()
    }

}


