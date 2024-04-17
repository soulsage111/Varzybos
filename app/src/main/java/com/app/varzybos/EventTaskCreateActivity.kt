package com.app.varzybos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.varzybos.data.Event
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.Date


class EventTaskCreateActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
                    val context = LocalContext.current
                    var taskName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var taskDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    val activity = (LocalContext.current as? Activity)
                    val intent = Intent();


                    Scaffold (
                        modifier =
                        Modifier
                            .fillMaxSize(),
                        topBar = {
                            CenterAlignedTopAppBar(

                                title = { Image(painter = painterResource(R.drawable.logo),"Logo", Modifier.height(70.dp)) },
                                navigationIcon = {
                                    IconButton(onClick = {

                                        activity?.finish()
                                    }) {
                                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            )
                        }

                    ){pad->
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(pad),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Spacer(modifier = Modifier.size(16.dp))

                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = taskName,
                                onValueChange = {taskName = it},
                                placeholder = { Text("Užduoties pavadinimas") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ))
                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = taskDescription,
                                onValueChange = {taskDescription = it},
                                placeholder = { Text("Aprašymas") },
                                singleLine = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.DarkGray,
                                    unfocusedTextColor = Color.DarkGray
                                ),
                                modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(
                                onClick = {
                                    try {
                                        intent.putExtra("name", taskName.text)
                                        intent.putExtra("description", taskDescription.text)
                                        setResult(RESULT_OK, intent)
                                        finish()
                                    } catch (e: Exception){
                                        Toast.makeText(context, "Klaida kuriant įvykį.", Toast.LENGTH_SHORT).show()
                                        Log.e(ContentValues.TAG, "Event Creation error", e)
                                    }

                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(46.dp)
                            ) {
                                Text("Išsaugoti", fontSize = 16.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.size(30.dp))
                        }
                    }
                }
            }
        }
    }
}