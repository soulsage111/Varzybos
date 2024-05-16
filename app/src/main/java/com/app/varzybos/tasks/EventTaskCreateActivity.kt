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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.varzybos.AdminScreen
import com.app.varzybos.MainViewModel
import com.app.varzybos.R
import com.app.varzybos.ui.theme.VarzybosTheme
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


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


                    var startPoint by remember {
                        mutableStateOf(GeoPoint(0.0, 0.0))
                    }
                    var endPoint by remember {
                        mutableStateOf(GeoPoint(0.0, 0.0))
                    }
                    var pointList = ArrayList<GeoPoint>()
                    var markerList = ArrayList<Marker>()


                    var currentPoint by remember {
                        mutableStateOf(GeoPoint(0.0, 0.0))
                    }


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


                                val mapView = MapView(context)
                                mapView.setTileSource(TileSourceFactory.MAPNIK)
                                mapView.setMultiTouchControls(true)

                                val mapController = mapView.controller
                                mapController.setZoom(15.0)
                                var startPoint = GeoPoint(48.8583, 2.2944) // Paris
                                mapController.setCenter(startPoint)


                                val mReceive: MapEventsReceiver = object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {

                                        currentPoint = p
                                        pointList.add(p)
                                        //isContext = true
                                        val marker3 = Marker(mapView)
                                        marker3.setPosition(p)
                                        marker3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        mapView.overlays.add(marker3)
                                        markerList.add(marker3)

                                        val roadManager: RoadManager =
                                            OSRMRoadManager(context, "varzybos")

                                        val road = roadManager.getRoad(pointList)

                                        val roadOverlay: Polyline =
                                            RoadManager.buildRoadOverlay(road)
                                        mapView.getOverlays().add(roadOverlay)




                                        mapView.invalidate()



                                        return true
                                    }

                                    override fun longPressHelper(p: GeoPoint): Boolean {

                                        return false
                                    }
                                }
                                val mapEventsOverlay = MapEventsOverlay(mReceive)
                                mapView.overlays.add(0, mapEventsOverlay)
                                mapView.invalidate()

                                Box(contentAlignment = Alignment.Center,
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
                                    AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                                        mapView
                                    })
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
