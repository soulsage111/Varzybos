package com.app.varzybos;


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.ui.theme.VarzybosTheme

class EventTaskActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var newTask by mutableStateOf(EventTask())
        var name = ""
        var description = ""
        var taskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null){
                    name = data.getStringExtra("name")!!
                    description = data.getStringExtra("description")!!
                    newTask = EventTask(name, description)
                }

            }
        }
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
                    mainViewModel.databaseService.initFirestore()
                    var globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current

                    var taskList: List<EventTask> = globalEvent.eventTasks

                    UpdateEvent(newTask, globalEvent, mainViewModel.databaseService) {
                        globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    }

                    Scaffold (
                        modifier =
                        Modifier
                            .fillMaxSize(),
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text(globalEvent.eventName) },
                                actions = {
                                    Image(painter = painterResource(R.drawable.logo),"Logo", Modifier.height(70.dp))
                                }
                            )
                        },
                        bottomBar = {
                            Button(onClick = {
                                var intent = Intent(context, EventTaskCreateActivity::class.java)
                                taskLauncher.launch(intent)

                                //prideti uzduoti
                            }) {
                                Image(Icons.Default.Add, contentDescription ="")
                            }
                        }
                    ){values ->
                        LazyColumn(Modifier.padding(values)) {
                            items(taskList){ task ->
                                Card(onClick = {
                                    //redaguoti paspausta uzd
                                }) {
                                    ListItem(
                                        headlineContent = { Text(task.taskName) },
                                        supportingContent = { Text(task.taskDescription)}
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

@Composable
fun UpdateEvent(
    newTask: EventTask,
    globalEvent: Event,
    databaseService: DatabaseService,
    function: () -> Unit
) {
    globalEvent.eventTasks = globalEvent.eventTasks + newTask
    databaseService.saveEvent(globalEvent)
    function
}
