package com.app.varzybos.events

import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.data.Event
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class EventActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            VarzybosTheme {
                Surface {
                    val activity = LocalContext.current as Activity
                    var sizeImage by remember { mutableStateOf(IntSize.Zero) }
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.databaseService.initFirestore()
                    var intent = activity.intent
                    var eventId = intent.getStringExtra("eventId")
                    var globalEvent : Event = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    var imageByteArray = ByteArray(1024*1024)

                    val storageRef = FirebaseStorage.getInstance().getReference()
                    var islandRef = storageRef.child("images/$eventId")
                    islandRef.getBytes(1024*1024).addOnSuccessListener {
                        data->
                        imageByteArray = data
                    }.addOnFailureListener{ e ->
                        Log.w(TAG, "Failed to retrieve image", e)
                    }
                    var imageBitmap by remember{ mutableStateOf(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)) }

                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White),
                        startY = sizeImage.height.toFloat()/3,  // 1/3
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
                            if (imageBitmap != null){
                                Box{
                                    Image(bitmap = imageBitmap.asImageBitmap(),
                                        contentDescription = "",
                                        modifier = Modifier.onGloballyPositioned {
                                            sizeImage = it.size
                                        })
                                    Box(modifier = Modifier
                                        .matchParentSize()
                                        .background(gradient))
                                }
                            }
                            Box(Modifier.padding(it)){
                                Text(globalEvent.eventName, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}