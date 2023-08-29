package com.satverse.suraksha.sos.contacts

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.satverse.suraksha.R

class CustomAdapter(
    private val context: Context,
    private val contacts: MutableList<ContactModel>
) : ArrayAdapter<ContactModel>(
    context, 0, contacts
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val c: ContactModel = getItem(position)!!

        val convertView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val tvName: TextView = convertView.findViewById(R.id.tvName)
        val tvPhone: TextView = convertView.findViewById(R.id.tvPhone)
        val ivDelete: ImageView = convertView.findViewById(R.id.ivDelete)

        tvName.text = c.getContact()
        tvPhone.text = c.getPhoneNumber()

        val db = DbHelper(context)

        ivDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you really want to delete?")
            builder.setCancelable(true)
            builder.setPositiveButton("Yes") { dialog: DialogInterface, id: Int ->
                db.deleteContact(c)
                contacts.remove(c)
                notifyDataSetChanged()
                Toast.makeText(context, "Contact removed!", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("No") { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }

        return convertView
    }

    fun refresh(list: List<ContactModel>) {
        contacts.clear()
        contacts.addAll(list)
        notifyDataSetChanged()
    }
}