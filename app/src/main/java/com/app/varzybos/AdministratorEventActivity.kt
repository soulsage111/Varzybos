package com.app.varzybos

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.data.Event
import com.app.varzybos.ui.theme.VarzybosTheme
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.Date

class AdministratorEventActivity: ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Interface()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Interface(){
    var activity = LocalContext.current as Activity
    var intent = activity.intent
    var eventId = intent.getStringExtra("eventId")
    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
    mainViewModel.databaseService.initFirestore()
    var globalEvent : Event = eventId?.let { mainViewModel.getEventFromId(it) }!!


    val context = LocalContext.current
    var eventName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(globalEvent.eventName))
    }
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(globalEvent.description))
    }
    val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Input, initialSelectedDateMillis = globalEvent.eventDate.time)

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
                placeholder = { Text("Renginio pavadinimas")},
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray
                ))
            Spacer(modifier = Modifier.size(16.dp))
            DatePicker(state = state, headline = null, title = null, showModeToggle = false)
            OutlinedTextField(value = description,
                onValueChange = {description = it},
                placeholder = { Text("Aprašymas")},
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray
                ),
                modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = {
                    try {
                        var startDate : Date
                        var event : Event = Event()
                        var milis: Long = state.selectedDateMillis!!
                        var instant = Instant.ofEpochMilli(milis)

                        startDate = Date.from(instant)
                        event.eventId = globalEvent.eventId
                        event.eventName = eventName.text
                        event.description = description.text
                        event.eventDate = startDate
                        mainViewModel.databaseService.initFirestore()
                        mainViewModel.databaseService.saveEvent(event)
                        //atsargiai gali nesulaukti kol issaugos
                        activity?.finish()
                    } catch (e: Exception){
                        Toast.makeText(context, "Klaida kuriant įvykį.", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Event Creation error")
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
