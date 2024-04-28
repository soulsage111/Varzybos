package com.app.varzybos.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.varzybos.MainViewModel
import com.app.varzybos.data.User
import com.app.varzybos.data.UserSingleton
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatActivity(values: PaddingValues) {
    var navController = rememberNavController()
    val mainViewModel: MainViewModel by viewModel<MainViewModel>()

    var reciever by remember {
        mutableStateOf(User())
    }
    mainViewModel.databaseService.initFirestore()
    mainViewModel.updateUsers()


//    var messages by remember{ mutableListOf(Message()) }

    val messages by UserSingleton.messages.observeAsState()

//    val messageObserver = Observer<ArrayList<Message>>{
//        messages.clear()
//        it.forEach { it ->
//            messages.add(it)
//        }
//        messages = it.toMutableList()
//    }
//
//    UserSingleton.messages.observe(LocalLifecycleOwner.current, messageObserver)


    NavHost(
        navController = navController, startDestination = "RecieverList", Modifier.padding(values)
    ) {
        composable(route = "RecieverList") {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = mainViewModel.userList.toList()) { item ->
                    var pressOffset by remember {
                        mutableStateOf(DpOffset.Zero)
                    }
                    var itemHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Card(modifier = Modifier.pointerInput(true) {
                        detectTapGestures(onPress = {
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        })
                    }) {
                        var isContextMenuVisible by rememberSaveable {
                            mutableStateOf(false)

                        }
                        ListItem(
                            headlineContent = { Text(item.name + " " + item.surname) },
                            supportingContent = { Text(item.email) },
                            modifier = Modifier.clickable(onClick = {
                                reciever = item
                                navController.navigate("ChatView")
                            })
                        )
                    }

                }

            }
        }
        composable(route = "ChatView") {
            ChatView(reciever, messages!!, mainViewModel)
        }
    }
}

@Composable
fun ChatView(reciever: User, messages: ArrayList<Message>, mainViewModel: MainViewModel) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        val (nameref, msgref, txtref) = createRefs()

        Column (Modifier.fillMaxWidth()
            .constrainAs(nameref){
                top.linkTo(parent.top)
                bottom.linkTo(msgref.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                height = Dimension.fillToConstraints
            }){
            Text(text = reciever.name + " " + reciever.surname)

            HorizontalDivider()
        }
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(msgref) {
                    top.linkTo(parent.top)
                    bottom.linkTo(txtref.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }) {
            items(items = messages.toList(), itemContent = { item ->

                if (item.to == FirebaseAuth.getInstance().currentUser?.uid.toString() && item.from == reciever.id) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.Start)
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(5.dp)
                        ) {
                            Text(text = item.message, Modifier.padding(10.dp))
                            Text(
                                text = millisToDate(item.timestamp),
                                Modifier
                                    .padding(5.dp)
                                    .align(Alignment.End)
                            )
                        }

                    }
                } else if (item.from == FirebaseAuth.getInstance().currentUser?.uid.toString() && item.to == reciever.id) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(5.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(text = item.message, Modifier.padding(10.dp))
                            Text(
                                text = millisToDate(item.timestamp),
                                Modifier
                                    .padding(5.dp)
                                    .align(Alignment.End)
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.size(4.dp))


            })
        }
        Spacer(modifier = Modifier.size(6.dp))

        OutlinedTextField(value = textFieldValue,
            onValueChange = { textFieldValue = it },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .constrainAs(txtref) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            trailingIcon = {
                IconButton(onClick = {
                    if (textFieldValue != TextFieldValue("")) {
                        mainViewModel.databaseService.sendMessage(
                            textFieldValue.text, reciever.id
                        )
                        textFieldValue = TextFieldValue("")
                    }

                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send, "Send"
                    )
                }
            })


    }
}


fun millisToDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return sdf.format(calendar.time)
}
