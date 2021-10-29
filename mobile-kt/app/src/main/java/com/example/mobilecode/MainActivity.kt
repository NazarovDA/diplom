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
import android.telephony.*
import android.widget.Button
import android.widget.TextView
import kotlinx.serialization.*


@Serializable
data class SignalInfo (val nwt: CellInfo, val opNum: String, val opTxt: String, val loc: Location) {
    
    val type = when (nwt.javaClass) {
        CellInfoLte::class.java -> ("LTE")
        CellInfoGsm::class.java -> ("GSM")
        CellInfoCdma::class.java -> ("CDMA")
        CellInfoNr::class.java -> ("Nr")
        CellInfoTdscdma::class.java -> ("TDSCMA")
        CellInfoWcdma::class.java -> ("WCDMA")
        else -> ("Underfined")
    }
    
    val connectionStatus = when (nwt.cellConnectionStatus) {
        CellInfo.CONNECTION_NONE -> "NONE"
        CellInfo.CONNECTION_PRIMARY_SERVING -> "PRIMARY_SERVING"
        CellInfo.CONNECTION_SECONDARY_SERVING -> "SECONDARY_SERVING"
        else -> "UNKNOWN"
    }
    
    val opetatorAlphaShort = nwt.cellIdentity.operatorAlphaShort
    val operatorAlphaLong = nwt.cellIdentity.operatorAlphaLong
    
    val signalLevel = when (nwt.cellSignalStrength.level) {
        1 -> "POOR"
        2 -> "MODERATE"
        3 -> "GOOD"
        4 -> "GREAT"
        else -> "UNKNOWN"
    }
    
    val asu = nwt.cellSignalStrength.asuLevel
    val dbm = nwt.cellSignalStrength.dbm
    
    val isRegistered = nwt.isRegistered

    val longitude = loc.longitude
    val latitude = loc.latitude

    override fun toString(): String {
        return "Тип подключения: $type\n" +
            "Местоположение: $longitude $latitude\n" +
            "Статус подключения: $connectionStatus\n" +
            "Информация об операторе: $operatorAlphaLong  $opetatorAlphaShort\n  " +
            "$opNum  $opTxt\n" +
            "Уровень сигнала: $signalLevel\n" +
            "ASU = $asu\n" +
            "DBM = $dbm"
    }
}



class MainActivity : AppCompatActivity(), LocationListener {
    
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var info: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Ya ustal build n2"
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
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            this
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {

        info = findViewById(R.id.SignalData)
        val allCells = telephonyManager.allCellInfo
        val operatorNumeric = telephonyManager.networkOperator // numeric name MCC+MNC
        val operatorText = telephonyManager.networkOperatorName // string variation of name

        if (allCells!=null) {
            var cells = mutableListOf<SignalInfo>()
            for (cell in allCells) {
                if (cell != null) {
                    cells.add(SignalInfo(cell, operatorNumeric, operatorText, location))
                }
                if (allCells.indexOf(cell) == 0) {
                    info.text = cells[0].toString()
                }
            }
        }
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

