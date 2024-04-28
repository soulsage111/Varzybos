package com.app.varzybos

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.app.varzybos.data.User
import com.app.varzybos.data.UserSingleton
import com.app.varzybos.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking

@ExperimentalMaterial3Api

class LoginActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Login(Modifier,  googleAuthUiClient)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Login(modifier: Modifier = Modifier,  googleAuthUiClient: GoogleAuthUiClient) {
    val image = painterResource(R.drawable.logo)
    var emailAddress by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("password"))
    }
    var showPassword by remember { mutableStateOf(value = true) }
    val fontColor = Color.DarkGray;
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    val mainViewModel : MainViewModel by viewModel<MainViewModel>()
    val localContext = LocalContext.current

    fun getGoogleLoginAuth(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(R.string.web_client_id.toString())
            .requestId()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(localContext, gso)
    }

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)
                    handleSignInResult(task)
                }
            }
        }
    
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
    ){
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(168.dp)
                .height(187.dp)
        )
        OutlinedTextField(
            value = emailAddress,
            onValueChange = { emailAddress = it },
            placeholder = {Text("El. paštas")},
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = fontColor,
                unfocusedTextColor = fontColor
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
            )
        Spacer(modifier = Modifier.size(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {Text("Slaptažodis")},
            singleLine = true,
            visualTransformation = if(showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth(0.8f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = fontColor,
                unfocusedTextColor = fontColor
            ),
            supportingText = {
                ClickableText(
                    text = AnnotatedString("Pamiršau slaptažodį"),
                    onClick = {},
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF837F88),

                        textAlign = TextAlign.Right,
                        textDecoration = TextDecoration.Underline,
                    )
                )
            },
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
                    auth.signInWithEmailAndPassword(emailAddress.text, password.text).addOnSuccessListener {
                        //mainViewModel.user.email = emailAddress.text

                        FirebaseApp.initializeApp(localContext)
                        mainViewModel.databaseService.initFirestore()
                        UserSingleton.initialize(auth.currentUser!!)
                        if (runBlocking {mainViewModel.databaseService.isAdmin(emailAddress.text)}){
                            var intent = Intent(localContext , AdminInterfaceActivity::class.java)
                            localContext.startActivity(intent)
                        } else {
                            var intent = Intent(localContext , InterfaceActivity::class.java)
                            localContext.startActivity(intent)
                        }

                    }.addOnFailureListener{e ->
                        Log.w(TAG, "Authorisation failure; ", e)
                        Toast.makeText(localContext, "Prisijungimas nepavyko", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Log.w(TAG, "Email not valid")
                    Toast.makeText(localContext, "El. pašto adresas netinkamas", Toast.LENGTH_SHORT).show() // in Activity
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(46.dp)
        ) {
            Text("Prisijungti", fontSize = 16.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.size(1.dp))
        Button(
            onClick = {
                var intent = Intent(localContext , RegistrationActivity::class.java)
                localContext.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(46.dp)
        ) {
            Text("Registruotis", fontSize = 16.sp, color = Color(0xFF837F88))
        }
        Spacer(modifier = Modifier.size(1.dp))
        Button(
            onClick = {
                startForResult.launch(getGoogleLoginAuth().signInIntent)
                runBlocking { googleAuthUiClient.signIn() }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(70.dp)
        ) {
            Image(painterResource(id = R.drawable.sign_in), "Google sign-in", modifier = Modifier
                .height(46.dp))
        }

        Spacer(modifier = Modifier.size(200.dp))
    }
}

fun handleSignInResult(task: Task<GoogleSignInAccount>) {

}


