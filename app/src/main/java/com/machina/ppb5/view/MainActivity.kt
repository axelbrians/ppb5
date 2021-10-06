package com.machina.ppb5.view

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.machina.ppb5.R
import com.machina.ppb5.data.model.Contact
import com.machina.ppb5.data.source.ContactDbSource
import com.machina.ppb5.databinding.ActivityMainBinding
import com.machina.ppb5.db.ContactDbHelper
import com.machina.ppb5.view.adapter.ContactListAdapter
import com.machina.ppb5.view.dialog.DialogContactForm

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ContactListAdapter

    private lateinit var dbHelper: SQLiteOpenHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var dbSource: ContactDbSource

    private val contacts = listOf(
        Contact("Moriarty", "+982131312313"),
        Contact("Ran", "+982131312313"),
        Contact("Yamato", "+982131312313"),
        Contact("Luffy", "+982131312313"),
        Contact("Kaido", "+982131312313"),
        Contact("Steelseries", "+982131312313"),
        Contact("Zephyrus", "+982131312313"),
        Contact("Gooju", "+982131312313")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ContactDbHelper(this)
        db = dbHelper.writableDatabase
        dbSource = ContactDbSource(db)

        contactAdapter = ContactListAdapter(
            this,
            R.layout.item_contact,
            dbSource.getAllContactEntries(),
            this::onItemDelete,
            this::onItemEdit
        )
        binding.mainListView.adapter = contactAdapter


        binding.insertBtn.setOnClickListener {
            DialogContactForm("Add new contact", "Name", "Phone", "Add", "Cancel", true, object : DialogContactForm.DialogAddItemListener {
                override fun onDialogPositiveClick(dialog: DialogFragment, contact: Contact) {
                    val res = dbSource.insertContactEntries(contact)
                    resolveDbChange()
                }

                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    dialog.dismiss()
                }
            }).show(supportFragmentManager, "insert dialog")
        }

        binding.searchBtn.setOnClickListener {
            DialogContactForm("Search contact by name", "Name", "Phone", "Search", "Cancel", false, object : DialogContactForm.DialogAddItemListener {
                override fun onDialogPositiveClick(dialog: DialogFragment, contact: Contact) {
                    contactAdapter = ContactListAdapter(
                        this@MainActivity,
                        R.layout.item_contact,
                        dbSource.searchEntries(contact.name),
                        this@MainActivity::onItemDelete,
                        this@MainActivity::onItemEdit
                    )
                    binding.mainListView.adapter = contactAdapter
                }

                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    resolveDbChange()
                    dialog.dismiss()
                }
            }).show(supportFragmentManager, "insert dialog")
        }
    }

    private fun onItemDelete(name: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete data with name: $name?")
            .setPositiveButton("Delete") { dialog, which ->
                val res = dbSource.deleteEntries(name)
                if (res) {
                    resolveDbChange()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun onItemEdit(name: String) {
        DialogContactForm("Edit contact $name", "Name", "Phone", "Save", "Cancel", true,
            object : DialogContactForm.DialogAddItemListener {
            override fun onDialogPositiveClick(dialog: DialogFragment, contact: Contact) {
                val res = dbSource.updateEntries(contact, name)
                if (res) {
                    resolveDbChange()
                }
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) {
                dialog.dismiss()
            }
        }).show(supportFragmentManager, "insert dialog")
    }

    private fun resolveDbChange() {
        contactAdapter = ContactListAdapter(
            this@MainActivity,
            R.layout.item_contact,
            dbSource.getAllContactEntries(),
            this@MainActivity::onItemDelete,
            this@MainActivity::onItemEdit
        )
        binding.mainListView.adapter = contactAdapter
    }
}