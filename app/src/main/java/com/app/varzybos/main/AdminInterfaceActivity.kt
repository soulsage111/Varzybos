package com.app.varzybos.main;

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.varzybos.AdminScreen
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.chat.ChatActivity
import com.app.varzybos.data.Event
import com.app.varzybos.data.User
import com.app.varzybos.UserSingleton
import com.app.varzybos.events.AdministratorEventActivity
import com.app.varzybos.events.ControlEventActivity
import com.app.varzybos.events.EventCreationActivity
import com.app.varzybos.statistics.StatisticsActivity
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api

class AdminInterfaceActivity : ComponentActivity() {
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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Interface(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
    mainViewModel.databaseService.initFirestore()
    mainViewModel.updateEvents()
    mainViewModel.updateUsers()

    FirebaseAuth.getInstance().currentUser?.let { it1 -> UserSingleton.initialize(it1) }

   // val eventList = mainViewModel.eventList.observeAsState()

    //val mainViewModel = MainActivity.mainViewModel

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        AdminScreen.Renginiai,
        AdminScreen.Vartotojai,
        AdminScreen.Pranesimai
    )

    var navController = rememberNavController()
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var scope = rememberCoroutineScope()
    var name by remember {
        mutableStateOf(UserSingleton.name)
    }
    var surname by remember {
        mutableStateOf(UserSingleton.surname)
    }
    var actionButtonEnabled by remember {
        mutableStateOf(false)
    }


    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text(UserSingleton.name + " " + UserSingleton.surname, modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Settings") },
                    icon = {Icon(Icons.Filled.Settings, "settings")},
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Atsijungti") },
                    selected = false,
                    icon = {Icon(Icons.AutoMirrored.Filled.ExitToApp, "log-out")},
                    onClick = {

                        var auth = FirebaseAuth.getInstance()
                        auth.signOut()
                        val packageManager: PackageManager = context.packageManager
                        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                        val componentName: ComponentName = intent.component!!
                        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                        context.startActivity(restartIntent)
                        Runtime.getRuntime().exit(0)
                    }
                )
            }
        },
        drawerState = drawerState
    ) {
        Scaffold (
            modifier =
            Modifier
                .fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {Image(painter = painterResource(R.drawable.logo),"Logo", Modifier.height(70.dp))},
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { mainViewModel.updateEvents() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            },
            floatingActionButton = {
                if(actionButtonEnabled){
                    FloatingActionButton(onClick = {
                        var intent = Intent(context , EventCreationActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Create, contentDescription = "Create event")
                    }
                }

            },
            bottomBar = {
                NavigationBar(
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = item.route) },
                            label = { Text(item.route) },
                            selected = selectedItem == index,
                            onClick = {
                                mainViewModel.updateEvents()
                                mainViewModel.updateUsers()
                                selectedItem = index
                                navController.navigate(item.route)
                            })
                    }
                }
            }
        ){values ->
//        LazyColumn(modifier = Modifier
//            .fillMaxSize()
//            .padding(values)){
//            items(items = mainViewModel.eventList, itemContent = {item ->
//                ListItem(headlineContent = { item.eventName })
//
//            })
//
//        }

            NavHost(navController = navController, startDestination = AdminScreen.Renginiai.route ){
                composable(route = AdminScreen.Renginiai.route){
                    actionButtonEnabled = true
                    EventList(mainViewModel.eventList, values, mainViewModel)
                }
                composable(route = AdminScreen.Vartotojai.route){
                    actionButtonEnabled = false
                    UserList(mainViewModel.userList, values, mainViewModel)
                }
                composable(route = AdminScreen.Pranesimai.route){
                    actionButtonEnabled = false
                    ChatActivity(values)
                }
            }

            LaunchedEffect(true) {
                //Do something when List end has been reached
                Toast.makeText(context, "ei", Toast.LENGTH_SHORT).show()
            }

            Spacer(modifier = Modifier.padding(100.dp))
            Log.w("Event list:", mainViewModel.eventList.toList().toString())
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventList(eventList: SnapshotStateList<Event>, values: PaddingValues, mainViewModel: MainViewModel){
    var context = LocalContext.current

//    var selectedItem by rememberSaveable {
//        mutableStateOf(Event())
//    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(values)) {
        items(items = eventList.toList()) { item ->
            //eventItem(event = item)
            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            var itemHeight by remember {
                mutableStateOf(0.dp)
            }
            Card (modifier = Modifier.pointerInput(true){
                detectTapGestures(
                    onPress = {
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            }) {
                var isContextMenuVisible by rememberSaveable {
                    mutableStateOf(false)

                }
                ListItem(
                    headlineContent = { Text(item.eventName) },
                    supportingContent = { Text(com.app.varzybos.chat.millisToDate(item.eventDate.toInstant().toEpochMilli())) },
                    modifier = Modifier.clickable(onClick = {
                        val intent = Intent(context, ControlEventActivity::class.java)
                        intent.putExtra("eventId", item.eventId)
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
                            expanded = isContextMenuVisible,
                            onDismissRequest = {
                                isContextMenuVisible = false
                            },
                            offset = pressOffset
                        ) {
                            DropdownMenuItem(text = { Text("Statistika") }, onClick = {
                                var intent = Intent(context, StatisticsActivity::class.java)
                                intent.putExtra("eventId", item.eventId)
                                context.startActivity(intent)
                                isContextMenuVisible = false
                            })
                            DropdownMenuItem(text = { Text("Redaguoti") }, onClick = {
                                var intent = Intent(context, AdministratorEventActivity::class.java)
                                intent.putExtra("eventId", item.eventId)
                                context.startActivity(intent)
                                isContextMenuVisible = false
                            })
                            DropdownMenuItem(text = { Text("Ištrinti") }, onClick = {
                                mainViewModel.databaseService.removeEvent(item)
                                mainViewModel.updateEvents()
                                isContextMenuVisible = false
                            })
                        }
                    }
                )
            }
            if (item == eventList.last()) {
                Spacer(modifier = Modifier.padding(35.dp))
            }
        }

    }
}

@Composable
private fun UserList(userList: SnapshotStateList<User>, values: PaddingValues, mainViewModel: MainViewModel){
    var context = LocalContext.current

//    var selectedItem by rememberSaveable {
//        mutableStateOf(Event())
//    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(values)) {
        items(items = userList.toList()) { item ->
            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            var itemHeight by remember {
                mutableStateOf(0.dp)
            }
            Card (modifier = Modifier.pointerInput(true){
                detectTapGestures(
                    onPress = {
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            }) {
                var isContextMenuVisible by rememberSaveable {
                    mutableStateOf(false)

                }
                ListItem(
                    headlineContent = { Text(item.name + " " + item.surname) },
                    supportingContent = { Text(item.email) },
                    modifier = Modifier.clickable(onClick = {
//                        Log.e(ContentValues.TAG, "Pasiclickino")
//                        var intent = Intent(context, AdministratorEventActivity::class.java)
//                        intent.putExtra("eventId", item.eventId)
//                        context.startActivity(intent)
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
                            expanded = isContextMenuVisible,
                            onDismissRequest = {
                                isContextMenuVisible = false
                            },
                            offset = pressOffset
                        ) {
                            DropdownMenuItem(text = { Text("Slaptažodžio keitimas") }, onClick = {
                                isContextMenuVisible = false
                                var auth = FirebaseAuth.getInstance()
                                auth.sendPasswordResetEmail(item.email).addOnSuccessListener {
                                    Toast.makeText(context, "Slaptažodžio keitimo laiškas išsiūstas", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener{e ->
                                    Toast.makeText(context, "Nepavyko išsiūsti laiško", Toast.LENGTH_SHORT).show()
                                    Log.e(TAG, "password email error", e)
                                }
                            })
                            DropdownMenuItem(text = { Text("Blokuoti vartotoją") }, onClick = {
                            })
                        }
                    }
                )
            }
            if (item == userList.last()) {
                Spacer(modifier = Modifier.padding(35.dp))
            }
        }

    }
}
