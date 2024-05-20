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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.data.Event
import com.app.varzybos.data.EventTask
import com.app.varzybos.ui.theme.VarzybosTheme

class StartEventTaskActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var newTask by mutableStateOf(EventTask())
        var name = ""
        var answer = ""
        var taskLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        name = data.getStringExtra("name")!!
                        answer = data.getStringExtra("answer")!!
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
                    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    var globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current

                    var taskList by remember { mutableStateOf(globalEvent.eventTasks) }

                    UpdateEvent(newTask, globalEvent, mainViewModel) {
                        globalEvent = eventId.let { mainViewModel.getEventFromId(it) }!!
                        taskList = globalEvent.eventTasks
                    }

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
                                        activity.finish()
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            )
                        }
                    ) { values ->
                        ListTasks(values, taskList) { task: EventTask ->
                            var intent = Intent(context, EventTaskAnswerActivity::class.java)
                            intent.putExtra("eventId", globalEvent.eventId)
                            intent.putExtra("taskId", task.taskId)
                            intent.putExtra("taskDescription", task.taskDescription)
                            intent.putExtra("taskName", task.taskName)
                            intent.putExtra("pointList", task.route)
                            taskLauncher.launch(intent)
                        }
                        Spacer(modifier = Modifier.padding(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateEvent(
    newTask: EventTask,
    globalEvent: Event,
    mainViewModel: MainViewModel,
    function: () -> Unit
) {
    if (newTask.taskName != "") {
        globalEvent.eventTasks.add(newTask)
        mainViewModel.saveEvent(globalEvent)
        function()
    }
}

@Composable
private fun ListTasks(
    values: PaddingValues,
    taskList: ArrayList<EventTask>,
    function: (name: EventTask) -> Unit
) {
    LazyColumn(Modifier.padding(values)) {
        items(taskList) { task ->
            Card(onClick = {
                function(task)
                //redaguoti paspausta uzd
            }) {
                ListItem(
                    headlineContent = { Text(task.taskName) },
                    supportingContent = { Text(task.taskDescription) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = "Back"
                        )
                    }
                )
            }
        }
    }
}
