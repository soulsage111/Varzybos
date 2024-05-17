package com.app.varzybos.tasks

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.GeoPoint
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import com.google.maps.model.DirectionsResult
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import kotlin.text.HexFormat


//import org.osmdroid.bonuspack.routing.OSRMRoadManager
//import org.osmdroid.bonuspack.routing.Road
//import org.osmdroid.bonuspack.routing.RoadManager
//import org.osmdroid.events.MapEventsReceiver
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.MapEventsOverlay
//import org.osmdroid.views.overlay.Marker
//import org.osmdroid.views.overlay.Polyline


class EventTaskCreateActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
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
                    val intent = Intent();

                    var navController = rememberNavController()


//                    var startPoint by remember {
//                        mutableStateOf(GeoPoint(0.0, 0.0))
//                    }
//                    var endPoint by remember {
//                        mutableStateOf(GeoPoint(0.0, 0.0))
//                    }
//                    var pointList = ArrayList<GeoPoint>()
//                    var markerList = ArrayList<Marker>()
//
//
//                    var currentPoint by remember {
//                        mutableStateOf(GeoPoint(0.0, 0.0))
//                    }


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
                                        imageVector = Icons.Default.ArrowBack,
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
                                    OutlinedTextField(value = taskName,
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
                                            .clickable(onClick = { navController.navigate("MapRoute") })
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

                                var isContext by remember {
                                    mutableStateOf(false)
                                }
                                var isStart by remember {
                                    mutableStateOf(true)
                                }


//                                val mapView = MapView(context)
//                                mapView.setTileSource(TileSourceFactory.MAPNIK)
//                                mapView.setMultiTouchControls(true)
//
//                                val mapController = mapView.controller
//                                mapController.setZoom(15.0)
//                                var startPoint = GeoPoint(48.8583, 2.2944) // Paris
//                                mapController.setCenter(startPoint)


//                                val mReceive: MapEventsReceiver = object : MapEventsReceiver {
//                                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
//
//                                        currentPoint = p
//                                        pointList.add(p)
//                                        //isContext = true
//                                        val marker3 = Marker(mapView)
//                                        marker3.setPosition(p)
//                                        marker3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                                        mapView.overlays.add(marker3)
//                                        markerList.add(marker3)
//
//                                        val roadManager: RoadManager =
//                                            OSRMRoadManager(context, "varzybos")
//
//                                        val road = roadManager.getRoad(pointList)
//
//                                        val roadOverlay: Polyline =
//                                            RoadManager.buildRoadOverlay(road)
//                                        mapView.getOverlays().add(roadOverlay)
//
//
//
//
//                                        mapView.invalidate()
//
//
//
//                                        return true
//                                    }
//
//                                    override fun longPressHelper(p: GeoPoint): Boolean {
//
//                                        return false
//                                    }
//                                }
//                                val mapEventsOverlay = MapEventsOverlay(mReceive)
//                                mapView.overlays.add(0, mapEventsOverlay)
//                                mapView.invalidate()

                                Box(contentAlignment = Alignment.TopCenter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(pad)
                                        .background(Color.Cyan)
                                        .pointerInput(Unit) {
                                            detectTapGestures {
                                                Log.d("TAG", "onCreate: $it")

                                            }

                                        }

                                ) {
//                                    AndroidView(modifier = Modifier.fillMaxSize(), factory = {
//                                        mapView
//                                    })


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

                                    var geoApi = GeoApiContext.Builder().apiKey("AIzaSyA7K7Gi_RWDrbWHrL6mlBfcaAtlxROAYbs").build()


                                    val singapore = LatLng(1.35, 103.87)
                                    val cameraPositionState = rememberCameraPositionState {
                                        position = CameraPosition.fromLatLngZoom(singapore, 10f)
                                    }
                                    val points = remember {
                                        mutableStateListOf<LatLng>()
                                    }

                                    val markers = remember {
                                        mutableStateListOf<LatLng>()
                                    }


                                    val polylineOptions =
                                        PolylineOptions().add(singapore).add(LatLng(1.28, 103.85))
                                            .add(LatLng(1.32, 103.90)).width(5f)
                                    ScaleBar(
                                        modifier = Modifier
                                            .padding(top = 5.dp, end = 15.dp)
                                            .align(Alignment.TopEnd),
                                        cameraPositionState = cameraPositionState
                                    )
                                    GoogleMap(modifier = Modifier.fillMaxSize(),
                                        cameraPositionState = cameraPositionState,
                                        onMapClick = {

                                            markers.add(it)

                                            val callback = object : PendingResult.Callback<DirectionsResult> {
                                                override fun onResult(result: DirectionsResult) {
                                                    Log.d("TAG", "onResult: $result")

                                                    result.routes.forEach {
                                                        val polylineOptions = PolylineOptions()
                                                        result.routes[0].overviewPolyline.decodePath().forEach {
                                                            polylineOptions.add(LatLng(it.lat, it.lng))
                                                            points.add(LatLng(it.lat, it.lng))
                                                        }

                                                    }
                                                }

                                                override fun onFailure(e: Throwable) {
                                                    Log.d("TAG", "onFailure: $e")
                                                }
                                            }
                                            var dest : com.google.maps.model.LatLng
                                            if(points.isNotEmpty()){
                                                dest = com.google.maps.model.LatLng(points.last().latitude, points.last().longitude)
                                            } else {
                                                dest = com.google.maps.model.LatLng(singapore.latitude, singapore.longitude)
                                            }
                                            var req = DirectionsApiRequest(geoApi)
                                            var ddd = com.google.maps.model.LatLng(it.latitude, it.longitude)
                                            req.origin(dest)

                                            req.destination(ddd).setCallback(callback)


                                            Toast.makeText(
                                                context, it.toString(), Toast.LENGTH_SHORT
                                            ).show()
                                        }) {
                                        markers.forEach {
                                            Marker(
                                                state = MarkerState(position = it)
                                            )
                                        }
                                        points.forEach {
                                            Polyline(points = points, color = Color.Red)
                                        }

                                    }
                                    Row {
//                                        val startCol = if(isStart) Color.Gray else MaterialTheme.colorScheme
//                                        val endCol = if(!isStart) Color.Gray else Color.Gray
//
//                                        Button(onClick = {
//
//                                        },
//                                            colors = ButtonDefaults.buttonColors(containerColor = startCol))
//                                        {
//                                            Text(text = "Pradžia")
//                                        }
//                                        Button(onClick = {
//
//                                        }) {
//                                            Text(text = "Pabaiga")
//                                        }

                                    }
                                }
                                }

                            composable(route = AdminScreen.Pranesimai.route) {
                                // image
                            }
                        }
                    }


                }
            }


        }
    }
}
