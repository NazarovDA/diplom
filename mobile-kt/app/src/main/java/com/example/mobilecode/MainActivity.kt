package com.example.mobilecode

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast

import android.location.LocationListener
import android.widget.Button
import android.widget.TextView

import android.telephony.CellInfo
import android.telephony.TelephonyManager

data class SignalInfo (
        val type: String,
        val operator: Pair<String, String>
        
    ) {
    val connection = type // тип подключения 
    val operatorInfo: Pair<String, String> = operator
    
}



class MainActivity : AppCompatActivity(), LocationListener {
    
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    
    private lateinit var cellInfo: CellInfo
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var info: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "KotlinApp"
        val button: Button = findViewById(R.id.getLocation)
        button.setOnClickListener {
            getLocation()
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
                ), locationPermissionCode)
        }
        
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            this
        )
        
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        val allCells = telephonyManager.allCellInfo
        val operatorNumeric = telephonyManager.networkOperator // numeric name MCC+MNC
        val operatorText = telephonyManager.networkOperatorName // string variation of name
       
        if (allCells!=null) {
            val nwt = allCells[0]
            
            val signalStrength = nwt.cellSignalStrength
            info = findViewById(R.id.SignalData)
            
            val level = when (signalStrength.level) {
                4 -> "Great"
                3 -> "Good"
                2 -> "Moderate"
                1 -> "Poor"
                else -> "None or Unknown"
            }
            val cellIdentityLte = nwt.cellIdentity.toString()
            
            info.text = "operator: $operatorText/$operatorNumeric \n" +
                "level: ${level}\n" +
                "asu: ${signalStrength.asuLevel}\n" +
                "dbm: ${signalStrength.dbm}\n" +
                "timestamp: ${nwt.timestampMillis}\n" +
                cellIdentityLte
        }
    }
    
    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        tvGpsLocation = findViewById(R.id.textView)
        tvGpsLocation.text = "Latitude: ${location.latitude}\nLongitude: ${location.longitude}"
    }
    
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}

