package com.app.varzybos.events

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.app.varzybos.R
import com.app.varzybos.ui.theme.VarzybosTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.varzybos.MainViewModel
import com.app.varzybos.data.Event
import com.app.varzybos.tasks.StartEventTaskActivity
import com.google.firebase.storage.FirebaseStorage
import kotlin.system.measureTimeMillis


class EventActivity: ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            VarzybosTheme {
                Surface {
                    val ONE_MEGABYTE: Long = 1024 * 1024;
                    val backgroundColor = MaterialTheme.colorScheme.background

                    val activity = LocalContext.current as Activity
                    var sizeImage by remember { mutableStateOf(IntSize.Zero) }
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    var intent = activity.intent
                    var eventId = intent.getStringExtra("eventId")
                    lateinit var globalEvent: Event
                    val t = measureTimeMillis {
                        globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    }
                    //Log.e("Time getEventFromId", t.toString())
                    var imageByteArray = ByteArray(1024*1024)
                    val context = LocalContext.current

                    val storageRef = FirebaseStorage.getInstance().getReference()
                    var islandRef = storageRef.child("images/$eventId")
                    var imageUri by remember {
                        mutableStateOf(Uri.parse(""))
                    }
                    var imageTask = islandRef.downloadUrl.addOnSuccessListener {uri ->
                        imageUri = uri
                    }.addOnFailureListener{e ->
                        Log.w(TAG, "Failed to retrieve image Uri", e)
                    }
                    var imageBitmap by remember{ mutableStateOf(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)) }

                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, backgroundColor),
                        startY = sizeImage.height.toFloat()/2,  // 1/3
                        endY = sizeImage.height.toFloat()
                    )
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(

                                title = { Image(painter = painterResource(R.drawable.logo),"Logo", Modifier.height(70.dp)) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        activity.finish()
                                    }) {
                                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            )
                        }
                    ) {it ->
                        Column (Modifier.padding(it)) {
                            if (imageUri != Uri.parse("")){
                                Box{
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .onGloballyPositioned {
                                                sizeImage = it.size
                                            }
                                            .height(200.dp)
                                            .fillMaxWidth()
                                    )
//                                    Image(bitmap = imageBitmap.asImageBitmap(),
//                                        contentDescription = "",
//                                        modifier = Modifier.onGloballyPositioned {
//                                            sizeImage = it.size
//                                        })
                                    Box(modifier = Modifier
                                        .matchParentSize()
                                        .background(gradient))
                                }
                            }
                            Box(contentAlignment = Alignment.Center){
                                Column (horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(globalEvent.eventName, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 30.sp)

                                    Text(com.app.varzybos.chat.millisToDate(globalEvent.eventDate.toInstant().toEpochMilli()), Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                            .verticalScroll(rememberScrollState())
                                            .weight(weight = 1f, fill = false)){
                                        Text("Aprašymas:", fontWeight = FontWeight.Bold)
                                        Text(globalEvent.description)
                                    }
                                    if(globalEvent.eventDate.toInstant().toEpochMilli() < System.currentTimeMillis()){
                                        Button(
                                            onClick = {
                                                var intent = Intent(context, StartEventTaskActivity::class.java)
                                                intent.putExtra("eventId", globalEvent.eventId)
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Text("Dalyvauti", fontSize = 16.sp, color = Color.White)
                                        }
                                    } else {
                                        var registered = mainViewModel.isRegisteredToEvent(eventId!!)


                                        Button(
                                            onClick = {
                                                val tm = measureTimeMillis {
                                                    mainViewModel.registerUserToEvent(eventId)
                                                }
                                                //Log.e("Time registerUserToEvent", tm.toString())

                                                finish()
                                            },
                                            enabled = !registered

                                        ) {
                                            Text("Registruotis", fontSize = 16.sp, color = Color.White)
                                        }
                                        if (registered){
                                            Button(
                                                onClick = {
                                                    mainViewModel.unregisterUserFromEvent(eventId)
                                                    finish()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                                            ) {
                                                Text("Išsiregistruoti", fontSize = 16.sp, color = Color(0xFF837F88))
                                            }
                                        }
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}