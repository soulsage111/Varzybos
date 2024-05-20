package com.app.varzybos

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.firebase.FirebaseApp
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


@ExperimentalMaterial3Api

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        //auth = Firebase.auth

//        var intent = Intent(this , MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
//        startActivity(intent)
        appContext = applicationContext
        requestLocationPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    fun requestLocationPermission() {
        val perms = arrayOf<String>(ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, perms.toString())) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Please grant the location permission",
                REQUEST_LOCATION_PERMISSION,
                perms.toString()
            )
        }
    }

    companion object {
        lateinit var appContext: Context
        private const val REQUEST_LOCATION_PERMISSION = 1
        //var mainViewModel: MainViewModel = MainViewModel(get())
    }

}


