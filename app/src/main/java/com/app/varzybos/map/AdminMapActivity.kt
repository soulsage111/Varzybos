package com.app.varzybos.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.varzybos.R
import com.app.varzybos.ui.theme.VarzybosTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pub.devrel.easypermissions.EasyPermissions

class AdminMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val context = applicationContext
        val intent = intent
        Configuration.getInstance().userAgentValue = context.packageName
        val mapView = MapView(context)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)

        var pointList = ArrayList<GeoPoint>()

        if (intent.hasExtra("pointList")) {
            pointList = intent.getSerializableExtra("pointList") as ArrayList<GeoPoint>
        }

        var markerList = ArrayList<Marker>()
        pointList.forEach {
            val marker = Marker(mapView)
            marker.setPosition(it)
            marker.setAnchor(
                Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
            )
            marker.icon = AppCompatResources.getDrawable(context, R.drawable.marker)
            markerList.add(marker)
        }

        var userLocation = GeoPoint(55.1694, 23.8813) // Lietuva


        val mapController = mapView.controller
        mapController.setCenter(userLocation)
        mapController.setZoom(5.0)


        // Warning nes nera iprastas permission tikrinimas

        if (EasyPermissions.hasPermissions(
                applicationContext,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        ) {

            runBlocking {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        pointList.add(0, GeoPoint(location!!.latitude, location.longitude))
                        pointList[0] = GeoPoint(location.latitude, location.longitude)
                    }.addOnFailureListener {
                        Log.w("TAG", "getLastLocation:exception", it)
                    }
            }
            val locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.setInterval(5000)
            locationRequest.setFastestInterval(2000)
            var locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    if (pointList.isNotEmpty()) {
                        pointList[0] = GeoPoint(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude
                        )

                    }
                    Log.e("TAG", "onLocationResult: $userLocation")

                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }


        var myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(0, myLocationOverlay)

        markerList.forEach {
            mapView.overlays.add(it)
        }

        if (pointList.isNotEmpty()) {
            val roadManager: RoadManager =
                OSRMRoadManager(context, "varzybos")

            val road = roadManager.getRoad(pointList)

            val roadOverlay: Polyline =
                RoadManager.buildRoadOverlay(road)
            mapView.overlays.add(roadOverlay)
        }


        mapView.invalidate()





        setContent {
            VarzybosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isStart by remember {
                        mutableStateOf(false)
                    }
                    val mReceive: MapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {

                            mapView.overlays.forEach {
                                if (it is Polyline) {
                                    mapView.overlays.remove(it)
                                }
                            }

                            val marker = Marker(mapView)
                            marker.setPosition(p)
                            marker.setAnchor(
                                Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
                            )
                            marker.icon = getDrawable(R.drawable.marker)
                            if (isStart) {
                                if (pointList.isEmpty()) {
                                    pointList.add(p)
                                    markerList.add(marker)
                                } else {
                                    pointList.add(1, p)
                                    markerList.add(0, marker)
                                }

                            } else {
                                pointList.add(p)
                                markerList.add(marker)
                            }

                            markerList.forEach {
                                mapView.overlays.add(it)
                            }


                            val roadManager: RoadManager =
                                OSRMRoadManager(context, "varzybos")

                            val road = roadManager.getRoad(pointList)

                            val roadOverlay: Polyline =
                                RoadManager.buildRoadOverlay(road)
                            mapView.overlays.add(roadOverlay)



                            mapView.invalidate()

                            return true
                        }

                        override fun longPressHelper(p: GeoPoint): Boolean {

                            return false
                        }
                    }
                    val mapEventsOverlay = MapEventsOverlay(mReceive)
                    mapView.overlays.add(1, mapEventsOverlay)
                    mapView.invalidate()

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    ) {
                        AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                            mapView
                        })
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row {
                                val startCol =
                                    if (!isStart) Color.Gray else MaterialTheme.colorScheme.primary
                                val endCol =
                                    if (isStart) Color.Gray else MaterialTheme.colorScheme.primary

                                Button(
                                    onClick = {
                                        isStart = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = startCol)
                                )
                                {
                                    Text(text = "Pradžia")
                                }
                                Button(
                                    onClick = {
                                        isStart = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = endCol)
                                ) {
                                    Text(text = "Pabaiga")
                                }

                            }
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Button(
                                    onClick = {
                                        mapView.overlays.forEach {
                                            if (it is Polyline) {
                                                mapView.overlays.remove(it)
                                            }
                                        }
                                        if (pointList.isNotEmpty()) {
                                            val roadManager: RoadManager =
                                                OSRMRoadManager(context, "varzybos")

                                            val road = roadManager.getRoad(pointList)

                                            val roadOverlay: Polyline =
                                                RoadManager.buildRoadOverlay(road)
                                            mapView.overlays.add(roadOverlay)
                                        }


                                        mapView.invalidate()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text(text = "Atnaujinti")
                                }
                                Button(onClick = {
                                    mapView.overlays.forEach {
                                        if (it is Polyline || it is Marker) {
                                            mapView.overlays.remove(it)
                                        }
                                    }
                                    pointList.clear()
                                    markerList.clear()
                                    mapView.invalidate()
                                }) {
                                    Text(text = "Išvalyti")
                                }
                                Button(onClick = {
                                    finish()
                                }) {
                                    Text(text = "Atšaukti")
                                }
                                Button(
                                    onClick = {
                                        pointList.removeAt(0)
                                        intent.putExtra("pointList", pointList)
                                        setResult(RESULT_OK, intent)
                                        finish()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                                ) {
                                    Text(text = "Išsaugoti")
                                }
                            }
                        }


                    }
                }
            }
        }
    }
}