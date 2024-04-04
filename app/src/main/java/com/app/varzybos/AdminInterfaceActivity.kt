package com.app.varzybos;

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    mainViewModel.startEventListening()

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

        EventList(mainViewModel.eventList, values)
        Log.w("Event list:", mainViewModel.eventList.toList().toString())
    }
}

@Composable
private fun EventList(eventList: SnapshotStateList<Event>, values: PaddingValues){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(values)) {
        items(items = eventList.toList(), itemContent = { item ->
            ListItem(headlineContent = { Text(item.eventName )}, supportingContent = {Text("Eventas")})

        })
    }
}



