package com.satverse.suraksha.sos.contacts

class ContactModel(val id: Int, initialName: String, initialPhoneNo: String) {
    private val phoneNo: String
    private var contactName: String

    // Constructor
    init {
        this.phoneNo = validate(initialPhoneNo)
        this.contactName = initialName
    }

    // Validate the phone number, and reformat if necessary
    private fun validate(phone: String): String {
        val cleanedPhone = phone.replace("-", "").replace(" ", "")
        return if (!cleanedPhone.startsWith("+")) {
            "+91$cleanedPhone"
        } else {
            cleanedPhone
        }
    }

    fun getPhoneNumber(): String {
        return phoneNo
    }

    fun getContact(): String {
        return contactName
    }

}
