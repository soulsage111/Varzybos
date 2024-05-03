package com.app.varzybos.tasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.ui.theme.VarzybosTheme
import java.util.UUID

class EventTaskAnswerActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
                    var eventId = intent.getStringExtra("eventId")!!
                    var taskName = intent.getStringExtra("taskName")!!
                    var taskDescription = intent.getStringExtra("taskDescription")!!
                    var taskId = intent.getStringExtra("taskId")!!
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    var globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current

                    var answer by remember { mutableStateOf(TextFieldValue(""))}

                    answer = TextFieldValue(mainViewModel.getAnswer(taskId).answer)


                    Scaffold(
                        modifier =
                        Modifier
                            .fillMaxSize(),
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
                                        finish()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            )
                        }
                    ) { values ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(values)) {
                            Text(
                                taskName,
                                Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 30.sp
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                taskDescription,
                                Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.size(30.dp))
                            OutlinedTextField(
                                value = answer,
                                onValueChange = { answer = it },
                                placeholder = {Text("Atsakymas")},
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                            )

                            Button(
                                onClick = {
                                    mainViewModel.saveAnswer(globalEvent.eventId, taskId, UUID.randomUUID().toString(), answer.text)
                                    finish()
                                }
                            ) {
                                Text("Išsaugoti", fontSize = 16.sp, color = Color.White)
                            }
                        }

                    }
                }
            }
        }
    }
}