package com.machina.ppb5.data.source

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.machina.ppb5.data.model.Contact
import com.machina.ppb5.db.table.ContactContract
import com.machina.ppb5.db.table.ContactContract.ContactEntry.COLUMN_NAME_NAME
import com.machina.ppb5.db.table.ContactContract.ContactEntry.COLUMN_NAME_PHONE
import com.machina.ppb5.db.table.ContactContract.ContactEntry.TABLE_NAME
import timber.log.Timber

class ContactDbSource(
    private val db: SQLiteDatabase
) {

    fun insertContactEntries(contact: Contact): Boolean {

        if (contact.name.isBlank() || contact.phone.isBlank()) {
            return false
        }

        val values = ContentValues().apply {
            put(COLUMN_NAME_PHONE, contact.phone)
            put(COLUMN_NAME_NAME, contact.name)
        }
        val resId = db.insert(TABLE_NAME, null, values)
        return resId >= 0
    }

    fun getAllContactEntries(): MutableList<Contact> {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(BaseColumns._ID, COLUMN_NAME_NAME, COLUMN_NAME_PHONE)
        // How you want the results sorted in the resulting Cursor
        val sortOrder = "$COLUMN_NAME_NAME DESC"

        val cursor = db.query(
            TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val contacts = mutableListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME_NAME))
                val phone = getString(getColumnIndexOrThrow(COLUMN_NAME_PHONE))

                contacts.add(Contact(name, phone))
            }
        }
        cursor.close()

        return contacts
    }

    fun searchEntries(name: String): List<Contact> {
        if (name.isBlank()) {
            return listOf()
        }
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(BaseColumns._ID, COLUMN_NAME_NAME, COLUMN_NAME_PHONE)

        // Filter results WHERE "title" = 'My Title'
        val selection = "$COLUMN_NAME_NAME = ?"
        val selectionArgs = arrayOf(name)

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "$COLUMN_NAME_NAME DESC"

        val cursor = db.query(
            TABLE_NAME,             // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,          // don't group the rows
            null,           // don't filter by row groups
            sortOrder               // The sort order
        )

        val contacts = mutableListOf<Contact>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME_NAME))
                val phone = getString(getColumnIndexOrThrow(COLUMN_NAME_PHONE))

                val contact = Contact(name, phone)
                contacts.add(contact)
            }
        }
        cursor.close()
        Timber.d("$contacts")

        return contacts
    }

    fun updateEntries(contact: Contact, name: String): Boolean {
        if (contact.phone.isBlank()) {
            return false
        }

        if (contact.name.isBlank()) {
            return false
        }

        val values = ContentValues().apply {
            put(COLUMN_NAME_NAME, contact.name)
            put(COLUMN_NAME_PHONE, contact.phone)
        }

        val selection = "$COLUMN_NAME_NAME = ?"
        val selectionArgs = arrayOf(name)
        val count = db.update(
            TABLE_NAME,
            values,
            selection,
            selectionArgs)

        return count != 0
    }

fun deleteEntries(name: String): Boolean {
        if (name.isBlank()) {
            return false
        }
        // Define 'where' part of query.
        val selection = "$COLUMN_NAME_NAME = ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(name)
        // Issue SQL statement.
        val deletedRows = db.delete(
            TABLE_NAME,
            selection,
            selectionArgs
        )

        return deletedRows != 0
    }
}