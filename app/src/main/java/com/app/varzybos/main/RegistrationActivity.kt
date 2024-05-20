package com.app.varzybos.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.data.User
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.system.measureTimeMillis

@ExperimentalMaterial3Api

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val image = painterResource(R.drawable.logo)
                    var emailAddress by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var surname by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var showPassword by remember { mutableStateOf(value = true) }
                    val fontColor = Color.DarkGray
                    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
                    val context = LocalContext.current

                    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
                    mainViewModel.initFirestore()

                    fun isValidEmail(email: String): Boolean {
                        return email.matches(emailRegex.toRegex())
                    }

                    Column(
                        modifier = Modifier
                            .width(360.dp)
                            .height(800.dp)
                            .background(color = Color(0xFFFFFFFF)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = image,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(168.dp)
                                .height(187.dp)
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Vardas") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = fontColor,
                                unfocusedTextColor = fontColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .testTag("vardas")
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        OutlinedTextField(
                            value = surname,
                            onValueChange = { surname = it },
                            placeholder = { Text("Pavardė") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = fontColor,
                                unfocusedTextColor = fontColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .testTag("pavarde")
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        OutlinedTextField(
                            value = emailAddress,
                            onValueChange = { emailAddress = it },
                            placeholder = { Text("El. paštas") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = fontColor,
                                unfocusedTextColor = fontColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .testTag("pastas"),
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Slaptažodis") },
                            singleLine = true,
                            visualTransformation = if (showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .testTag("slaptazodis"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = fontColor,
                                unfocusedTextColor = fontColor
                            ),
                            trailingIcon = {
                                if (showPassword) {
                                    IconButton(onClick = { showPassword = false }) {
                                        Icon(
                                            imageVector = Icons.Filled.Visibility,
                                            contentDescription = "hide_password"
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { showPassword = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.VisibilityOff,
                                            contentDescription = "hide_password"
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Button(
                            onClick = {
                                if (isValidEmail(emailAddress.text)) {
                                    var auth: FirebaseAuth = Firebase.auth
                                    auth.createUserWithEmailAndPassword(
                                        emailAddress.text,
                                        password.text
                                    ).addOnSuccessListener {
                                        var user: User = User()
                                        user.name = name.text
                                        user.surname = surname.text
                                        user.email = emailAddress.text
                                        val t = measureTimeMillis {
                                            mainViewModel.saveUser(user)
                                        }
                                        //Log.e("Time saveUser", t.toString())


                                        Log.d(TAG, "registerInWithEmail:success")
                                        Toast.makeText(
                                            context,
                                            "Registracija sėkminga",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Registracija nepavyko",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.w(TAG, "registerInWithEmail:failure")
                                    }

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Netinkamas el. pašto adresas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(46.dp)
                                .testTag("reg")
                        ) {
                            Text("Registruotis", fontSize = 16.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.size(1.dp))
                        Button(
                            onClick = {
                                finish()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(46.dp)
                        ) {
                            Text("Grįžti", fontSize = 16.sp, color = Color(0xFF837F88))
                        }

                        Spacer(modifier = Modifier.size(200.dp))
                    }
                }
            }
        }
    }
}


