package com.app.varzybos.main;

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.Screen
import com.app.varzybos.chat.ChatActivity
import com.app.varzybos.data.Event
import com.app.varzybos.data.User
import com.app.varzybos.UserSingleton
import com.app.varzybos.chat.millisToDate
import com.app.varzybos.events.EventActivity
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import org.koin.core.time.measureDuration
import kotlin.system.measureTimeMillis

@ExperimentalMaterial3Api

class InterfaceActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
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
    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
    mainViewModel.initFirestore()
    mainViewModel.updateEvents()
    FirebaseAuth.getInstance().currentUser?.let { it1 ->
        UserSingleton.initialize(
            it1, mainViewModel
        )
    }

    var u = User()
    u.id = FirebaseAuth.getInstance().currentUser?.uid.toString()
    u.name = UserSingleton.name
    u.surname = UserSingleton.surname
    u.email = UserSingleton.email
    mainViewModel.saveUser(u)

    var scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Screen.Renginiai, Screen.ManoRenginiai, Screen.Pranesimai
    )

    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var navController = rememberNavController()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    UserSingleton.name + " " + UserSingleton.surname,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
//                NavigationDrawerItem(label = { Text(text = "Settings") },
//                    icon = { Icon(Icons.Filled.Settings, "settings") },
//                    selected = false,
//                    onClick = { /*TODO*/ })
                NavigationDrawerItem(label = { Text(text = "Atsijungti") },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, "log-out") },
                    onClick = {

                        var auth = FirebaseAuth.getInstance()
                        auth.signOut()
                        val packageManager: PackageManager = context.packageManager
                        val intent: Intent =
                            packageManager.getLaunchIntentForPackage(context.packageName)!!
                        val componentName: ComponentName = intent.component!!
                        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                        context.startActivity(restartIntent)
                        Runtime.getRuntime().exit(0)
                    })

            }
        }, drawerState = drawerState
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            CenterAlignedTopAppBar(title = {
                Image(
                    painter = painterResource(R.drawable.logo), "Logo", Modifier.height(70.dp)
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
            }, actions = {
                IconButton(onClick = { mainViewModel.updateEvents() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Menu")
                }
            })
        }, bottomBar = {
            NavigationBar(
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(icon = {
                        var ico = Icons.Filled.Brightness1
                        if (item == Screen.Renginiai) {
                            ico = Icons.Filled.Attractions
                        }
                        if (item == Screen.ManoRenginiai) {
                            ico = Icons.Filled.ChecklistRtl
                        }
                        if (item == Screen.Pranesimai) {
                            ico = Icons.AutoMirrored.Filled.Message
                        }
                        Icon(ico, contentDescription = item.route)
                    },
                        label = { Text(item.route) },
                        selected = selectedItem == index,
                        onClick = {
                            mainViewModel.updateEvents()
                            selectedItem = index
                            navController.navigate(item.route)

                        },
                        modifier = modifier.testTag(item.route)
                    )
                }
            }
        }) { values ->
            NavHost(navController = navController, startDestination = Screen.Renginiai.route) {
                composable(route = Screen.Renginiai.route) {
                    EventList(mainViewModel.eventList, values, mainViewModel)
                }
                composable(route = Screen.ManoRenginiai.route) {
                    EventList(mainViewModel.userEventList, values, mainViewModel)
                }
                composable(route = Screen.Pranesimai.route) {
                    ChatActivity(values)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventList(
    eventList: SnapshotStateList<Event>, values: PaddingValues, mainViewModel: MainViewModel
) {
    var context = LocalContext.current

    val storageRef = FirebaseStorage.getInstance().getReference()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(values),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = eventList.toList()) { item ->

            var sizeImage by remember { mutableStateOf(IntSize.Zero) }
            val gradient = Brush.verticalGradient(
                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                startY = sizeImage.height.toFloat() / 2,  // 1/3
                endY = sizeImage.height.toFloat()
            )

            var islandRef = storageRef.child("images/${item.eventId}")
            var imageUri by remember {
                mutableStateOf(Uri.parse(""))
            }
            var imageTask = islandRef.downloadUrl.addOnSuccessListener { uri ->
                imageUri = uri
            }.addOnFailureListener { e ->
                Log.w(TAG, "Failed to retrieve image Uri", e)
            }

            //eventItem(event = item)
            if (item.eventDate.toInstant()
                    .toEpochMilli() > System.currentTimeMillis() || (item.registeredUsers.contains(
                    FirebaseAuth.getInstance().currentUser?.uid!!
                ) && !item.closed)
            ) {
                var pressOffset by remember {
                    mutableStateOf(DpOffset.Zero)
                }
                ElevatedCard(modifier = Modifier
                    .pointerInput(true) {
                        detectTapGestures(onPress = {
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        })
                    }
                    .padding(5.dp)) {
                    AsyncImage(model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .onGloballyPositioned {
                                sizeImage = it.size
                            }
                            .height(200.dp)
                            .fillMaxWidth())
                    ListItem(headlineContent = { Text(item.eventName) }, supportingContent = {
                        Text(
                            millisToDate(
                                item.eventDate.toInstant().toEpochMilli()
                            )
                        )
                    }, modifier = Modifier.clickable(onClick = {

                        var intent = Intent(context, EventActivity::class.java)
                        intent.putExtra("eventId", item.eventId)
                        System.currentTimeMillis()
                        context.startActivity(intent)
                    })
                    )
                }
            }


        }

    }
}




