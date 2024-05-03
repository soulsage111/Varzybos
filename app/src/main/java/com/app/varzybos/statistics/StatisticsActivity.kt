package com.app.varzybos.statistics

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.data.Answer
import com.app.varzybos.data.EventTask
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth
import com.jaikeerthick.composable_graphs.composables.line.LineGraph
import com.jaikeerthick.composable_graphs.composables.line.model.LineData
import java.util.UUID

class StatisticsActivity : ComponentActivity() {
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
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    var activity = LocalContext.current as Activity
                    var sourceIntent = activity.intent
                    var eventId = sourceIntent.getStringExtra("eventId")
                    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    mainViewModel.updateUsers()
                    var globalEvent = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current

                    var taskList by remember { mutableStateOf(globalEvent.eventTasks) }

                    var scores = mainViewModel.getScores(eventId)

                    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                        CenterAlignedTopAppBar(

                            title = {
                                Image(
                                    painter = painterResource(R.drawable.logo),
                                    "Logo",
                                    Modifier.height(70.dp)
                                )
                            }, navigationIcon = {
                                IconButton(onClick = {
                                    activity.finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                    }) { values ->
                        if(scores.size != 0){
                            Column(
                                Modifier
                                    .padding(values)
                                    .fillMaxSize()
                            ) {
                                val data: MutableList<LineData> = mutableListOf()
                                scores.forEach { item ->
                                    data.add(LineData("", item.value))

                                }


                                LineGraph(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    data = data
                                )
                                val column1Weight = .7f // 30%
                                val column2Weight = .3f // 70%
                                // The LazyColumn will be our table. Notice the use of the weights below
                                LazyColumn(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    // Here is the header
                                    item {
                                        Row(Modifier.background(Color.Gray)) {
                                            TableCell(text = "Dalyvis", weight = column1Weight)
                                            TableCell(text = "Rezultatas", weight = column2Weight)
                                        }
                                    }
                                    // Here are all the lines of your table.
                                    scores.toSortedMap().forEach {
                                        item {
                                            Row(Modifier.fillMaxWidth()) {
                                                TableCell(text = name(mainViewModel, it.key), weight = column1Weight)
                                                TableCell(
                                                    text = it.value.toString(),
                                                    weight = column2Weight
                                                )
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
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .fillMaxSize()
    )
}

fun name(mainViewModel: MainViewModel, userId: String): String {
    mainViewModel.userList.forEach {
        if (it.id == userId){
            return it.name+" "+it.surname
        }
    }
    return ""
}