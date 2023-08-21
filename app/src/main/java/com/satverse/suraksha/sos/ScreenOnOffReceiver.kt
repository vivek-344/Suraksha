package com.satverse.suraksha.sos

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class ScreenOnOffReceiver : BroadcastReceiver() {

    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var Value1: String? = null
    var Value2: String? = null
    var Value3: String? = null
    var Value4: String? = null
    var Value5: String? = null
    var Value: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context.applicationContext)

        val getShared: SharedPreferences =
            context.getSharedPreferences("demo", Context.MODE_PRIVATE)
        Value1 = getShared.getString("phone1", "")?.trim { it <= ' ' }
        Value2 = getShared.getString("phone2", "")?.trim { it <= ' ' }
        Value3 = getShared.getString("phone3", "")?.trim { it <= ' ' }
        Value4 = getShared.getString("phone4", "")?.trim { it <= ' ' }
        Value5 = getShared.getString("phone5", "")?.trim { it <= ' ' }

        getLocationAndSendMessage(context)
    }

    private fun getLocationAndSendMessage(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationProviderClient?.lastLocation?.addOnCompleteListener { task ->
            val location: Location? = task.result
            if (location != null) {
                sendLocationMessage(context, location)
            }
        }
    }

    private fun sendLocationMessage(context: Context, location: Location) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            location.latitude, location.longitude, 1
        )!!

        val message = "I am at ${addresses[0].latitude}, ${addresses[0].longitude}, " +
                "${addresses[0].countryName}, ${addresses[0].locality}, ${addresses[0].getAddressLine(0)}"

        val phoneNumberList = listOf(Value1, Value2, Value3, Value4, Value5)
        val smsManager = SmsManager.getDefault()
        for (phoneNumber in phoneNumberList) {
            if (!phoneNumber.isNullOrBlank()) {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }
        }
    }

    companion object {
        const val SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG"
    }
}