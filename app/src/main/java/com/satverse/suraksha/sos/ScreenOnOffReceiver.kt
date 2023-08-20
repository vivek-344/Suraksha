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
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.satverse.suraksha.R
import okhttp3.internal.concurrent.Task
import java.io.IOException
import java.util.Locale

class ScreenOnOffReceiver : BroadcastReceiver() {
    var mediaPlayer: MediaPlayer? = null
    private var powerBtnTapCount = 0
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var Value1: String? = null
    var Value2: String? = null
    var Value3: String? = null
    var Value4: String? = null
    var Value: String? = null
    var timer = 0

    // Time is in millisecond so 50sec = 50000 I have used
    // countdown Interveal is 1sec = 1000 I have used
    var countDownTimer: CountDownTimer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timer++
            Log.d(SCREEN_TOGGLE_TAG, "time$timer")
        }

        override fun onFinish() {
            timer = 0
            powerBtnTapCount = 0
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.getAction()
        if ((Intent.ACTION_SCREEN_OFF == action || Intent.ACTION_SCREEN_ON == action) && powerBtnTapCount == 0) {
            powerBtnTapCount++
            countDownTimer.start()
            Log.d(SCREEN_TOGGLE_TAG, "Power button tapped in timer $timer  $powerBtnTapCount")
        } else if (powerBtnTapCount > 0) {
            if (timer < 30) {
                if (Intent.ACTION_SCREEN_OFF == action || Intent.ACTION_SCREEN_ON == action) {  // ACTION_SCREEN_OFF :- read by just tapping on it where this is used
                    powerBtnTapCount++
                    Log.d(
                        SCREEN_TOGGLE_TAG,
                        "Power button tapped in timer $timer  $powerBtnTapCount"
                    )
                }
            } else {
                powerBtnTapCount = 0
                if (Intent.ACTION_SCREEN_OFF == action || Intent.ACTION_SCREEN_ON == action) {  // ACTION_SCREEN_OFF :- read by just tapping on it where this is used
                    powerBtnTapCount++
                    countDownTimer.start()
                    Log.d(
                        SCREEN_TOGGLE_TAG,
                        "Power button tapped in timer $timer  $powerBtnTapCount"
                    )
                }
            }
        }
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.police_siren)
        }
        if (powerBtnTapCount == 6) { //?
            mediaPlayer?.start()
            mediaPlayer?.setLooping(true)
            powerBtnTapCount = 0
            countDownTimer.cancel()
            timer = 0
        }
        if (powerBtnTapCount == 3) { // ?
            if (mediaPlayer?.isPlaying() == true) {
                mediaPlayer?.stop()
                mediaPlayer?.setLooping(false)
                mediaPlayer = null
            }
            //Getting the value of shared preference back
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context.applicationContext)
            val getShared: SharedPreferences =
                context.getSharedPreferences("demo", Context.MODE_PRIVATE) // demo?
            Value1 = getShared.getString("phone1", "")?.trim { it <= ' ' }
            Value2 = getShared.getString("phone2", "")?.trim { it <= ' ' }
            Value3 = getShared.getString("phone3", "")?.trim { it <= ' ' }
            Value4 = getShared.getString("phone4", "")?.trim { it <= ' ' }
            Value =
                getShared.getString("msg", "I am in danger, please come fast...")?.trim { it <= ' ' }
            tryIt(context)
        }
    }

    fun tryIt(context: Context) {
        SendLocationMessage(context)
        if (Value1!!.trim { it <= ' ' } != "") {
            /**
             * ContextCompat :- It is a class for replacing some work with base context.
             * For example if you used before something like getContext().getColor(R.color.black);
             * Now its deprecated since android 6.0 (API 22+) so you should use: getContext().getColor(R.color.black,theme);
             */
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                makeCall(context)
            }
        }
    }

    //Calling function
    private fun makeCall(context: Context) {
        //Calling function
        val intent =
            Intent(Intent.ACTION_CALL) // ACTION_CALL :- read about it by tapping on where it is used
        val phoneNumber = Value1
        intent.setData(Uri.parse("tel:$phoneNumber")) // intent.setData() and Uri.parse() :- read about it by tapping on where it is used


        // setFlags(), FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TASK :- read about these all by tapping on where it is used
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        //        intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.d("Called on  ", Value1!!)
    }

    private fun SendLocationMessage(context: Context) {

        // ACCESS_FINE_LOCATION, PERMISSION_GRANTED, PackageManager and ACCESS_COARSE_LOCATION :-   read about these all by tapping on where it is used
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient?.getLastLocation()
            ?.addOnCompleteListener(object : OnCompleteListener<Location?> {
                override fun onComplete(task: com.google.android.gms.tasks.Task<Location?>) {
                    //Initialize Location
                    val location: Location? = task.getResult()
                    var Message = Value
                    try {


                        // Locale.getDefault() :- read about these all by tapping on where it is used
                        //Initialize Geocoder
                        val geocoder = Geocoder(context, Locale.getDefault())
                        //Initialize adress list
                        val addresses: MutableList<Address>? =
                            location?.let {
                                geocoder.getFromLocation( // geocoder.getFromLocation, location.getLatitude() and location.getLongitude() :-  read about these all by tapping on where it is used
                                    it.latitude, location.longitude, 1
                                )
                            }
                        Message = Message + "I am at " + (addresses?.get(0)?.latitude ) +
                                "," + (addresses?.get(0)?.longitude ) + ", " + (addresses?.get(0)?.countryName) +
                                "," + (addresses?.get(0)?.locality ) + ", " + (addresses?.get(0)
                            ?.getAddressLine(
                                0
                            ) )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val phoneNumber1 = Value1
                    val phoneNumber2 = Value2
                    val phoneNumber3 = Value3
                    val phoneNumber4 = Value4
                    Log.d("Message sent as  ", "$Value1 $Value2 $Value3 $Value4 $Value ")
                    if (Value1 != "" || Value2 != "" || Value3 != "" || Value4 != "") {
                        if (Value1 != "") {

                            // SmsManager, SmsManager.getDefault () and smsManager.sendTextMessage()  :- read about these all by tapping on where it is used
                            val smsManager = SmsManager.getDefault()
                            smsManager.sendTextMessage(phoneNumber1, null, Message, null, null)
                        }
                        if (Value2 != "") {
                            val smsManager = SmsManager.getDefault()
                            smsManager.sendTextMessage(phoneNumber2, null, Message, null, null)
                        }
                        if (Value3 != "") {
                            val smsManager = SmsManager.getDefault()
                            smsManager.sendTextMessage(phoneNumber3, null, Message, null, null)
                        }
                        if (Value4 != "") {
                            val smsManager = SmsManager.getDefault()
                            smsManager.sendTextMessage(phoneNumber4, null, Message, null, null)
                        }
                    }
                }
            })
    }

    companion object {
        const val SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG"
    }
}