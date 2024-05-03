package com.app.varzybos.events

import android.app.Activity
import android.content.ContentValues
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.data.Event
import com.app.varzybos.data.User
import com.app.varzybos.tasks.AdministratorEventTaskActivity
import com.app.varzybos.tasks.AdministratorEventTaskEvaluationActivity
import com.app.varzybos.tasks.EventTaskActivity
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth

class ControlEventActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val activity = LocalContext.current as Activity
                    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()
                    var intent = activity.intent
                    var eventId = intent.getStringExtra("eventId")
                    var globalEvent: Event = eventId?.let { mainViewModel.getEventFromId(it) }!!
                    val context = LocalContext.current

                    var isContextMenuVisible by rememberSaveable {
                        mutableStateOf(false)
                    }

                    mainViewModel.currentUserList =
                        mainViewModel.getUsersFromList(globalEvent.registeredUsers)


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
                            }, actions = {
                                IconButton(onClick = {
                                    isContextMenuVisible = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Back"
                                    )
                                    DropdownMenu(expanded = isContextMenuVisible,
                                        onDismissRequest = {
                                            isContextMenuVisible = false
                                        }) {
                                        if (mainViewModel.isEventStopped(eventId)) {
                                            DropdownMenuItem(text = { Text("Leisti renginį") },
                                                onClick = {
                                                    mainViewModel.stoppedEvent(
                                                        eventId, false
                                                    )
                                                })
                                        } else {
                                            DropdownMenuItem(text = {
                                                Text(
                                                    "Stabdyti renginį", color = Color.Red
                                                )
                                            }, onClick = {
                                                mainViewModel.stoppedEvent(
                                                    eventId, true
                                                )
                                            })
                                        }

                                    }
                                }
                            })
                    }

                    ) { pad ->
                        if (mainViewModel.isEventStopped(eventId)){
                            UserEvaluationList(
                                userList = mainViewModel.currentUserList,
                                values = pad,
                                mainViewModel = mainViewModel,
                                eventId
                            )
                        } else {
                            UserList(
                                userList = mainViewModel.currentUserList,
                                values = pad,
                                mainViewModel = mainViewModel,
                                eventId
                            )
                        }



                    }

                }
            }
        }
    }
}

@Composable
private fun UserList(
    userList: SnapshotStateList<User>,
    values: PaddingValues,
    mainViewModel: MainViewModel,
    eventId: String
) {
    var context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(values)
    ) {
        items(items = userList.toList()) { item ->
            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            var itemHeight by remember {
                mutableStateOf(0.dp)
            }
            Card(
                modifier = Modifier.pointerInput(true) {
                    detectTapGestures(onPress = {
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    })
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceTint)
            ) {
                var isContextMenuVisible by rememberSaveable {
                    mutableStateOf(false)

                }
                ListItem(headlineContent = { Text(item.name + " " + item.surname) },
                    supportingContent = {
                        Text("El. Paštas: " + item.email + "\nMokėjimo statusas: " + "kazkoks bus")
                    },
                    modifier = Modifier.clickable(onClick = {

                        if (mainViewModel.isEventStopped(eventId)){
                            var intent = Intent(context, AdministratorEventTaskEvaluationActivity::class.java)
                            intent.putExtra("eventId", eventId)
                            intent.putExtra("userId", item.id)
                            context.startActivity(intent)
                        } else {
                            var intent = Intent(context, AdministratorEventTaskActivity::class.java)
                            intent.putExtra("eventId", eventId)
                            intent.putExtra("userId", item.id)
                            context.startActivity(intent)

                        }


                    }),
                    trailingContent = {
                        IconButton(onClick = {
                            isContextMenuVisible = true
                            //selectedItem = item
                            //meniu kazkoks
                        }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = isContextMenuVisible, onDismissRequest = {
                                isContextMenuVisible = false
                            }, offset = pressOffset
                        ) {
                            DropdownMenuItem(text = { Text("Šalinti iš žaidimo") }, onClick = {
                                isContextMenuVisible = false

                            })

                        }
                    })

            }
            if (item == userList.last()) {
                Spacer(modifier = Modifier.padding(35.dp))
            }
        }

    }
}

@Composable
private fun UserEvaluationList(
    userList: SnapshotStateList<User>,
    values: PaddingValues,
    mainViewModel: MainViewModel,
    eventId: String
) {
    var context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(values)
    ) {
        items(items = userList.toList()) { item ->
            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            var itemHeight by remember {
                mutableStateOf(0.dp)
            }
            Card(
                modifier = Modifier.pointerInput(true) {
                    detectTapGestures(onPress = {
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    })
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceTint)
            ) {
                var isContextMenuVisible by rememberSaveable {
                    mutableStateOf(false)

                }
                ListItem(headlineContent = { Text(item.name + " " + item.surname) },
                    supportingContent = {
                        Text("El. Paštas: " + item.email + "\nMokėjimo statusas: " + "kazkoks bus")
                    },
                    modifier = Modifier.clickable(onClick = {
                        Log.e(ContentValues.TAG, "Pasiclickino")
                        var intent = Intent(context, AdministratorEventTaskEvaluationActivity::class.java)
                        intent.putExtra("eventId", eventId)
                        intent.putExtra("userId", item.id)
                        context.startActivity(intent)
                    }),
                    trailingContent = {
                        IconButton(onClick = {
                            isContextMenuVisible = true
                            //selectedItem = item
                            //meniu kazkoks
                        }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = isContextMenuVisible, onDismissRequest = {
                                isContextMenuVisible = false
                            }, offset = pressOffset
                        ) {
                            DropdownMenuItem(text = { Text("Šalinti iš žaidimo") }, onClick = {
                                isContextMenuVisible = false

                            })

                        }
                    })

            }
            if (item == userList.last()) {
                Spacer(modifier = Modifier.padding(35.dp))
            }
        }

    }
}
