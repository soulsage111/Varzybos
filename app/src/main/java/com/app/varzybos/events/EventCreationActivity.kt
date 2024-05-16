package com.app.varzybos.events

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.data.Event
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import kotlin.system.measureTimeMillis

class EventCreationActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        val (bitmap, updateBitmap) = mutableStateOf<Bitmap?>(null)
        var imageUri : Uri by mutableStateOf(Uri.parse("android.resource://com.app.varzybos/" + R.drawable.img))

        var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                updateBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, uri))
                imageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    val context = LocalContext.current
                    var eventName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var eventTime by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(TextFieldValue(""))
                    }
                    val stateDate = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
                    var eventDate: Date
                    val activity = (LocalContext.current as Activity)
                    val regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]"

                    var enableButton by remember { mutableStateOf(value = true) }

                    fun isValidTime(time: String): Boolean {
                        return time.matches(regex.toRegex())
                    }

                    Scaffold (
                        modifier =
                        Modifier
                            .fillMaxSize(),
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

                    ){pad->
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(pad)
                            .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Spacer(modifier = Modifier.size(16.dp))
//                            ImageDisplay(bitmap, {
//                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                            }, resources)
                            Box(modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .clickable {
                                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = eventName,
                                onValueChange = {eventName = it},
                                placeholder = { Text("Renginio pavadinimas")},
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ))
                            Spacer(modifier = Modifier.size(16.dp))
                            DatePicker(state = stateDate, headline = null, title = null, showModeToggle = false)
                            OutlinedTextField(value = eventTime,
                                onValueChange = {eventTime = it},
                                placeholder = { Text("00:00")},
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ))
                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = description,
                                onValueChange = {description = it},
                                placeholder = { Text("Aprašymas")},
                                singleLine = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp)
                                    .fillMaxWidth(0.8f))
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(
                                onClick = {
                                    try {
                                        enableButton = false
                                        var startDate : Date
                                        var event : Event = Event()
                                        var milis: Long = 0
                                        if (isValidTime(eventTime.text)){
                                            val dateFormat = SimpleDateFormat("HH:mm")
                                            val parsed = dateFormat.parse(eventTime.text)

                                            if (stateDate.selectedDateMillis!=null){
                                                milis = stateDate.selectedDateMillis!! + parsed.toInstant().toEpochMilli()
                                            }
                                            var instant = Instant.ofEpochMilli(milis)

                                            startDate = Date.from(instant)
                                            event.eventId = UUID.randomUUID().toString()
                                            event.eventName = eventName.text
                                            event.description = description.text
                                            event.eventDate = startDate
                                            if (bitmap != null) {
                                                //event.thumbnail = bitmap.asImageBitmap()
                                            }

                                            val storageRef = FirebaseStorage.getInstance().getReference("images/"+event.eventId)
                                            runBlocking {
                                                storageRef.putFile(imageUri).await()
                                            }

                                            mainViewModel.initFirestore()
                                            val t = measureTimeMillis {
                                                mainViewModel.saveEvent(event)
                                            }
                                            //Log.e("Time saveEvent", t.toString())
                                            activity?.finish()
                                            enableButton = true

                                        }

                                    } catch (e: Exception){
                                        enableButton = true
                                        Toast.makeText(context, "Klaida kuriant įvykį.", Toast.LENGTH_SHORT).show()
                                        Log.e(TAG, "Event Creation error", e)
                                    }

                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(46.dp),
                                enabled = enableButton
                            ) {
                                Text("Išsaugoti", fontSize = 16.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.size(30.dp))
                        }
                    }
                }
            }
        }
    }
}


