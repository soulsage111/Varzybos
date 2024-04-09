package com.app.varzybos;

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.data.Event
import com.app.varzybos.ui.theme.VarzybosTheme

@ExperimentalMaterial3Api

class AdminInterfaceActivity : ComponentActivity() {
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Interface(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
    mainViewModel.databaseService.initFirestore()
    mainViewModel.updateEvents()

   // val eventList = mainViewModel.eventList.observeAsState()

    //val mainViewModel = MainActivity.mainViewModel

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Visi renginiai", "Mano renginiai", "Pranešimai")

    Scaffold (
        modifier =
            Modifier
            .fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {Image(painter = painterResource(R.drawable.logo),"Logo", Modifier.height(70.dp))},
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { mainViewModel.updateEvents() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Menu")
                        }
                    }
                )
            },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                var intent = Intent(context , EventCreationActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Create, contentDescription = "Create event")
            }
        },
            bottomBar = {
                NavigationBar(
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {
                                mainViewModel.updateEvents()
                                selectedItem = index
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

        EventList(mainViewModel.eventList, values, mainViewModel)

            LaunchedEffect(true) {
                //Do something when List end has been reached
                Toast.makeText(context, "ei", Toast.LENGTH_SHORT).show()
            }

        Spacer(modifier = Modifier.padding(100.dp))
        Log.w("Event list:", mainViewModel.eventList.toList().toString())
    }
}

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
                    supportingContent = { Text("Eventas") },
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
                            DropdownMenuItem(text = { Text("Redaguoti") }, onClick = {
                                Log.e(ContentValues.TAG, "Pasiclickino")
                                var intent = Intent(context, AdministratorEventActivity::class.java)
                                intent.putExtra("eventId", item.eventId)
                                context.startActivity(intent)
                                isContextMenuVisible = false
                            })
                            DropdownMenuItem(text = { Text("Ištrinti") }, onClick = {
                                Log.e(ContentValues.TAG, "Pasiclickino")
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
