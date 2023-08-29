package com.satverse.suraksha.sos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.satverse.suraksha.R
import com.satverse.suraksha.sos.contacts.ContactModel
import com.satverse.suraksha.sos.contacts.CustomAdapter
import com.satverse.suraksha.sos.contacts.DbHelper

class EmergencyContactsActivity : AppCompatActivity() {

    private var button: ImageView? = null
    private var listView: ListView? = null
    private var db: DbHelper? = null
    private var list: List<ContactModel>? = null
    private var customAdapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            askIgnoreOptimization()
        }

        button = findViewById(R.id.add_contact)
        listView = findViewById<View>(R.id.ListView) as ListView
        db = DbHelper(this)
        list = db!!.allContacts
        customAdapter = (list as MutableList<ContactModel>?)?.let { CustomAdapter(this, it) }
        listView!!.adapter = customAdapter

        button?.setOnClickListener {
            if (db!!.count() != 5) {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                @Suppress("DEPRECATION")
                startActivityForResult(intent, PICK_CONTACT)
            } else {
                Toast.makeText(
                    this@EmergencyContactsActivity,
                    "Can't add more than 5 contacts!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent("emergency_contacts_destroyed")
        sendBroadcast(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this,
                    "Permissions Denied!\nCan't use the App!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_CONTACT -> if (resultCode == RESULT_OK) {
                val contactData = data!!.data
                val c = contentResolver.query(contactData!!, null, null, null, null)
                c?.let {
                    if (it.moveToFirst()) {
                        val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        @SuppressLint("Range") val hasPhone = it.getString(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        var phone: String? = null
                        try {
                            if (hasPhone.equals("1", ignoreCase = true)) {
                                val phones = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null,
                                    null
                                )
                                phones?.let { phonesCursor ->
                                    if (phonesCursor.moveToFirst()) {
                                        phone = phonesCursor.getString(phonesCursor.getColumnIndex("data1"))
                                    }
                                    phonesCursor.close()
                                }
                            }
                            val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            phone?.let { phoneNumber ->
                                db?.addcontact(ContactModel(0, name, phoneNumber))
                                list = db?.allContacts
                                list?.let { contactsList ->
                                    customAdapter?.refresh(contactsList)
                                }
                            }
                        } catch (ex: Exception) {
                            // Handle exception
                        }
                    }
                    it.close()
                }
            }
        }
    }

    @SuppressLint("BatteryLife")
    private fun askIgnoreOptimization() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:$packageName")
        @Suppress("DEPRECATION")
        startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST)
    }

    companion object {
        private const val IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002
        private const val PICK_CONTACT = 1
    }
}