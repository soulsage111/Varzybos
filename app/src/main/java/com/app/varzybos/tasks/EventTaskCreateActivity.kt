package com.app.varzybos.tasks

//import com.google.firebase.firestore.GeoPoint


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.varzybos.AdminScreen
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.map.AdminMapActivity
import com.app.varzybos.ui.theme.VarzybosTheme
import org.osmdroid.util.GeoPoint


class EventTaskCreateActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var pointListFromIntent = ArrayList<GeoPoint>()
        var taskLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        pointListFromIntent =
                            data.getSerializableExtra("pointList") as ArrayList<GeoPoint>
                    }

                }
            }



        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel by viewModel<MainViewModel>()
                    val context = LocalContext.current
                    var taskName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    var taskDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    val activity = (LocalContext.current as? Activity)
                    val intent = Intent()

                    var navController = rememberNavController()
                    var pointList = remember { ArrayList<GeoPoint>() }


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

                                    activity?.finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                    }

                    ) { pad ->
                        NavHost(navController = navController, startDestination = "TaskRoute") {
                            composable(route = "TaskRoute") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(pad),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.size(16.dp))

                                    Spacer(modifier = Modifier.size(16.dp))
                                    OutlinedTextField(
                                        value = taskName,
                                        onValueChange = { taskName = it },
                                        placeholder = { Text("Užduoties pavadinimas") },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.DarkGray,
                                            unfocusedTextColor = Color.DarkGray
                                        )
                                    )
                                    Spacer(modifier = Modifier.size(16.dp))
                                    OutlinedTextField(
                                        value = taskDescription,
                                        onValueChange = { taskDescription = it },
                                        placeholder = { Text("Aprašymas") },
                                        singleLine = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.DarkGray,
                                            unfocusedTextColor = Color.DarkGray
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(0.8f)
                                    )
                                    Spacer(modifier = Modifier.size(16.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clickable(onClick = {
                                                if (pointListFromIntent.isNotEmpty()) {
                                                    pointList = pointListFromIntent
                                                }
                                                //navController.navigate("MapRoute")
                                                var mapIntent =
                                                    Intent(context, AdminMapActivity::class.java)
                                                mapIntent.putExtra("pointList", pointList)
                                                taskLauncher.launch(mapIntent)
                                            })
                                            .background(color = Color.Gray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Map,
                                            contentDescription = "Map",
                                            modifier = Modifier.size(48.dp),
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(16.dp))

                                    Button(
                                        onClick = {
                                            try {
                                                intent.putExtra("name", taskName.text)
                                                intent.putExtra("description", taskDescription.text)
                                                intent.putExtra("pointList", pointList)
                                                setResult(RESULT_OK, intent)
                                                finish()
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Klaida kuriant įvykį.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.e(ContentValues.TAG, "Event Creation error", e)
                                            }

                                        }, colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(
                                                0xFF6750A4
                                            )
                                        ), modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(46.dp)
                                    ) {
                                        Text("Išsaugoti", fontSize = 16.sp, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.size(30.dp))
                                }
                            }
                            composable(route = "MapRoute") {
                                navController.navigate("TaskRoute")


                                //GOOGLE directions api is slow, so we use OSRM with OSMDroid


//                                    val client = mainViewModel.httpClient
//
//                                    var JSON = "application/json".toMediaType();
//
//                                    @Throws(IOException::class)
//                                    fun postHttp(): String{
//                                    val request = Request.Builder()
//                                        .url(url)
//                                        .build()
//
//                                }


//                                    var geoApi = GeoApiContext.Builder().apiKey("AIzaSyA7K7Gi_RWDrbWHrL6mlBfcaAtlxROAYbs").build()
//
//
//                                    val singapore = LatLng(1.35, 103.87)
//                                    val cameraPositionState = rememberCameraPositionState {
//                                        position = CameraPosition.fromLatLngZoom(singapore, 10f)
//                                    }
//                                    val points = remember {
//                                        mutableStateListOf<LatLng>()
//                                    }
//
//                                    val markers = remember {
//                                        mutableStateListOf<LatLng>()
//                                    }
//
//
//                                    val polylineOptions =
//                                        PolylineOptions().add(singapore).add(LatLng(1.28, 103.85))
//                                            .add(LatLng(1.32, 103.90)).width(5f)
//                                    ScaleBar(
//                                        modifier = Modifier
//                                            .padding(top = 5.dp, end = 15.dp)
//                                            .align(Alignment.TopEnd),
//                                        cameraPositionState = cameraPositionState
//                                    )
//                                    GoogleMap(modifier = Modifier.fillMaxSize(),
//                                        cameraPositionState = cameraPositionState,
//                                        onMapClick = {
//
//                                            if (isStart){
//                                                markers.add(0, it)
//                                            } else {
//                                                markers.add(it)
//                                            }
//
//
//                                            val callback = object : PendingResult.Callback<DirectionsResult> {
//                                                override fun onResult(result: DirectionsResult) {
//                                                    Log.d("TAG", "onResult: $result")
//
//                                                    result.routes.forEach {
//                                                        val polylineOptions = PolylineOptions()
//                                                        result.routes[0].overviewPolyline.decodePath().forEach {
//                                                            polylineOptions.add(LatLng(it.lat, it.lng))
//                                                            points.add(LatLng(it.lat, it.lng))
//                                                        }
//
//                                                    }
//                                                }
//
//                                                override fun onFailure(e: Throwable) {
//                                                    Log.d("TAG", "onFailure: $e")
//                                                }
//                                            }
//                                            var dest : com.google.maps.model.LatLng
//                                            if(points.isNotEmpty()){
//                                                dest = com.google.maps.model.LatLng(points.last().latitude, points.last().longitude)
//                                            } else {
//                                                dest = com.google.maps.model.LatLng(singapore.latitude, singapore.longitude)
//                                            }
//                                            var req = DirectionsApiRequest(geoApi)
//                                            var ddd = com.google.maps.model.LatLng(it.latitude, it.longitude)
//                                            req.origin(dest)
//
//                                            req.destination(ddd).setCallback(callback)
//
//
//                                            Toast.makeText(
//                                                context, it.toString(), Toast.LENGTH_SHORT
//                                            ).show()
//                                        }) {
//                                        markers.forEach {
//                                            if (markers.first() == it){
//                                                Marker(
//                                                    state = MarkerState(position = it),
//                                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
//                                                )
//                                            } else if (markers.last() == it) {
//                                                Marker(
//                                                    state = MarkerState(position = it)
//                                                )
//                                            } else {
//                                                Marker(
//                                                    state = MarkerState(position = it),
//                                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
//                                                )
//                                            }
//                                        }
//                                        points.forEach {
//                                            Polyline(points = points, color = Color.Red)
//                                        }
//
//                                    }
//                                    Row {
//                                        val startCol = if(isStart) Color.Gray else MaterialTheme.colorScheme.primary
//                                        val endCol = if(!isStart) Color.Gray else MaterialTheme.colorScheme.primary
//
//                                        Button(onClick = {
//                                            isStart = true
//                                        },
//                                            colors = ButtonDefaults.buttonColors(containerColor = startCol))
//                                        {
//                                            Text(text = "Pradžia")
//                                        }
//                                        Button(onClick = {
//                                            isStart = false
//                                        },
//                                            colors = ButtonDefaults.buttonColors(containerColor = endCol)) {
//                                            Text(text = "Pabaiga")
//                                        }
//
//                                    }

                            }

                            composable(route = AdminScreen.Pranesimai.route) {
                                // image addition to task
                            }
                        }
                    }


                }
            }


        }
    }
}
