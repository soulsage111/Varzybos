package com.app.varzybos.events

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.chat.millisToDate
import com.app.varzybos.data.Event
import com.app.varzybos.tasks.EventTaskActivity
import com.app.varzybos.ui.theme.VarzybosTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AdministratorEventActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var activity = LocalContext.current as Activity
                    var intent = activity.intent
                    var eventId = intent.getStringExtra("eventId")
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    var globalEvent : Event = eventId?.let { mainViewModel.getEventFromId(it) }!!


                    val context = LocalContext.current
                    var eventName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(globalEvent.eventName))
                    }
                    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(globalEvent.description))
                    }

                    val stateDate = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

                    var stateTime by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }

                    var isContextMenuVisible by rememberSaveable {
                        mutableStateOf(false)
                    }

                    val regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]"
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
                                        activity?.finish()
                                    }) {
                                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        isContextMenuVisible = true
                                    }) {
                                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Back")
                                        DropdownMenu(
                                            expanded = isContextMenuVisible,
                                            onDismissRequest = {
                                                isContextMenuVisible = false
                                            }
                                        ) {
                                            DropdownMenuItem(text = { Text("Redaguoti užduotis") }, onClick = {
                                                Log.e(ContentValues.TAG, "Pasiclickino")
                                                var intent = Intent(context, EventTaskActivity::class.java)
                                                intent.putExtra("eventId", eventId)
                                                context.startActivity(intent)
                                                isContextMenuVisible = false
                                            })
                                        }
                                    }
                                }
                            )
                        }

                    ){pad->
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(pad),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = eventName,
                                onValueChange = {eventName = it},
                                placeholder = { Text("Renginio pavadinimas") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ))
                            Spacer(modifier = Modifier.size(16.dp))
                            DatePicker(state = stateDate, headline = null, title = null, showModeToggle = false)
                            OutlinedTextField(value = stateTime,
                                onValueChange = {stateTime = it},
                                placeholder = { Text("00:00")},
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ))
                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = description,
                                onValueChange = {description = it},
                                placeholder = { Text("Aprašymas") },
                                singleLine = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ),
                                modifier = Modifier.weight(1f)
                                    .fillMaxWidth(0.8f))
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(
                                onClick = {
                                    try {
                                        var milis: Long = 0
                                        if (isValidTime(stateTime.text)) {
                                            var startDate: Date
                                            var event: Event = Event()

                                            val dateFormat = SimpleDateFormat("HH:mm")
                                            val parsed = dateFormat.parse(stateTime.text)

                                            if (stateDate.selectedDateMillis != null) {
                                                milis =
                                                    stateDate.selectedDateMillis!! + parsed.toInstant()
                                                        .toEpochMilli()
                                            }

                                            var milis: Long = 0
                                            if (stateDate.selectedDateMillis != null) {
                                                if (parsed != null) {
                                                    milis =
                                                        stateDate.selectedDateMillis!! + parsed.toInstant()
                                                            .toEpochMilli()
                                                }
                                            }
                                            var instant = Instant.ofEpochMilli(milis)
                                            globalEvent = mainViewModel.getEventFromId(eventId)

                                            startDate = Date.from(instant)
                                            event.eventId = globalEvent.eventId
                                            event.eventName = eventName.text
                                            event.description = description.text
                                            event.eventDate = startDate
                                            event.registeredUsers = globalEvent.registeredUsers
                                            event.eventTasks = globalEvent.eventTasks
                                            mainViewModel.initFirestore()
                                            mainViewModel.saveEvent(event)
                                            //atsargiai gali nesulaukti kol issaugos
                                            activity.finish()
                                        }
                                    } catch (e: Exception){
                                        Toast.makeText(context, "Klaida kuriant įvykį.", Toast.LENGTH_SHORT).show()
                                        Log.e(ContentValues.TAG, "Event Creation error")
                                    }

                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(46.dp)
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
