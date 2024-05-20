package com.app.varzybos.tasks

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.ui.theme.VarzybosTheme

class ViewTaskAnswerActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val activity = LocalContext.current as Activity
                    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    var intent = activity.intent
                    var eventId = intent.getStringExtra("eventId")
                    //var globalEvent : Event = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current



                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(

                                title = {
                                    Image(
                                        painter = painterResource(R.drawable.logo),
                                        "Logo",
                                        Modifier.height(70.dp)
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        activity.finish()
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            )
                        }) { pad ->
                        Text(text = "as")
//                            Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(pad)){
//                                Text(globalEvent.eventName, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 30.sp)
//
//                                Text(globalEvent.eventDate.toString(), Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//
//                                Column(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .padding(20.dp)){
//                                    Text("Aprašymas:", fontWeight = FontWeight.Bold)
//                                    Text(globalEvent.description)
//                                }
//                                if(globalEvent.eventDate.toInstant().toEpochMilli() < System.currentTimeMillis()){
//                                    Button(
//                                        onClick = {
//                                            var intent = Intent(context, StartEventTaskActivity::class.java)
//                                            intent.putExtra("eventId", globalEvent.eventId)
//                                            context.startActivity(intent)
//                                        }
//                                    ) {
//                                        Text("Dalyvauti", fontSize = 16.sp, color = Color.White)
//                                    }
//                                } else {
//                                    var registered = mainViewModel.isRegisteredToEvent(eventId)
//
//
//                                    Button(
//                                        onClick = {
//                                            mainViewModel.registerUserToEvent(eventId)
//                                            finish()
//                                        },
//                                        enabled = !registered
//
//                                    ) {
//                                        Text("Registruotis", fontSize = 16.sp, color = Color.White)
//                                    }
//                                    if (registered){
//                                        Button(
//                                            onClick = {
//                                                mainViewModel.unregisterUserFromEvent(eventId)
//                                                finish()
//                                            },
//                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
//                                        ) {
//                                            Text("Išsiregistruoti", fontSize = 16.sp, color = Color(0xFF837F88))
//                                        }
//                                    }
//                                }
//                            }
                    }
                }
            }
        }
    }
}