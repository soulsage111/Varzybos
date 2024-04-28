package com.app.varzybos.tasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.data.Answer
import com.app.varzybos.data.EventTask
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class AdministratorEventTaskEvaluationActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var newTask by mutableStateOf(EventTask())
        var name = ""
        var description = ""
        var id = ""
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var activity = LocalContext.current as Activity
                    var sourceIntent = activity.intent
                    var eventId = sourceIntent.getStringExtra("eventId")
                    var userId = sourceIntent.getStringExtra("userId")!!
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.databaseService.initFirestore()
                    var globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current

                    var taskList by remember{ mutableStateOf(globalEvent.eventTasks) }

                    var answers = mainViewModel.databaseService.getAnswersForUser(eventId, userId )

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
                    ){values ->
                        var textFieldValue = remember {
                            TextFieldValue("")
                        }
                        LazyColumn(Modifier.padding(values)) {
                            items(taskList){ task ->
                                ElevatedCard(onClick = {
//                                    var intent = Intent(context, ViewTaskAnswerActivity::class.java)
//                                    intent.putExtra("name", task.taskName)
                                }, modifier = Modifier.padding(4.dp)) {
                                    ListItem(
                                        headlineContent = { Text(task.taskName) },
                                        supportingContent = { Text(task.taskDescription + "\nAtsakymas:\n" + findAnswer(task.taskId, answers!!)) }
                                    )
                                    OutlinedTextField(
                                        value = textFieldValue,
                                        onValueChange = {
                                        textFieldValue = it
                                            findAnswer(task.taskId, answers!!)},

                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(100.dp))
                    }
                }
            }
        }
    }
}

private fun findAnswer(taskId:String, answers: ArrayList<Answer>): String{
    answers.forEach{ans ->
        if(ans.taskId == taskId){
            return ans.answer
        }
    }
    return ""
}

private fun scoreAnswer(taskId:String, answers: ArrayList<Answer>, score: Int){
    answers.forEach{ans ->
        if(ans.taskId == taskId){
            ans.score = score
        }
    }
}