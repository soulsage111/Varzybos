package com.app.varzybos

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.app.varzybos.data.Event
import com.app.varzybos.ui.theme.VarzybosTheme
import java.time.Instant
import java.util.Date

class EventCreationActivity: ComponentActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Interface(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var eventName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var eventDate: Date
    val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    Scaffold (
        modifier =
        Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Image(painter = painterResource(R.drawable.logo),"Logo", Modifier.height(70.dp)) },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
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
                    var db : DatabaseService = DatabaseService()
                    var startDate : Date
                    var event : Event = Event()
                    var milis: Long = state.selectedDateMillis!!
                    var instant = Instant.ofEpochMilli(milis)

                    startDate = Date.from(instant)
                    event.eventId = System.currentTimeMillis().toString()
                    event.eventName = eventName.text
                    event.description = description.text
                    event.eventDate = startDate
                    db.saveEvent(event)
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
