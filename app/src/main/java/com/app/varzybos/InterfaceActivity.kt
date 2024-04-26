package com.app.varzybos;

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.app.varzybos.data.Event
import com.app.varzybos.data.UserSingleton
import com.app.varzybos.events.EventActivity
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth

@ExperimentalMaterial3Api

class InterfaceActivity : ComponentActivity() {
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
    FirebaseAuth.getInstance().currentUser?.let { it1 -> UserSingleton.initialize(it1) }

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Screen.Renginiai,
        Screen.ManoRenginiai,
        Screen.Pranesimai
    )

    var navController = rememberNavController()


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
                            selectedItem = index
                            navController.navigate(item.route)
                        })
                }
            }
        }
    ){values ->
        NavHost(navController = navController, startDestination = Screen.Renginiai.route ){
            composable(route = Screen.Renginiai.route){
                EventList(mainViewModel.eventList, values, mainViewModel)
            }
            composable(route = Screen.ManoRenginiai.route){
                EventList(mainViewModel.userEventList, values, mainViewModel)
            }
        }
    }
}

@Composable
private fun EventList(eventList: SnapshotStateList<Event>, values: PaddingValues, mainViewModel: MainViewModel){
    var context = LocalContext.current

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(values)) {
        items(items = eventList.toList()) { item ->
            //eventItem(event = item)
            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            Card (modifier = Modifier.pointerInput(true){
                detectTapGestures(
                    onPress = {
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            }) {
                ListItem(
                    headlineContent = { Text(item.eventName) },
                    supportingContent = { Text("Eventas") },
                    modifier = Modifier.clickable(onClick = {
                        Log.e(ContentValues.TAG, "Pasiclickino")
                        var intent = Intent(context, EventActivity::class.java)
                        intent.putExtra("eventId", item.eventId)
                        context.startActivity(intent)
                    })
                )
            }
        }

    }
}




