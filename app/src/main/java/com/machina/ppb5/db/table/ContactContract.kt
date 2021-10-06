package com.machina.ppb5.db.table

import android.provider.BaseColumns

object ContactContract {

    object ContactEntry: BaseColumns {
        const val TABLE_NAME = "table_contact"
        const val COLUMN_NAME_PHONE = "phone"
        const val COLUMN_NAME_NAME = "name"

    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${ContactEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${ContactEntry.COLUMN_NAME_PHONE} TEXT," +
                "${ContactEntry.COLUMN_NAME_NAME} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ContactEntry.TABLE_NAME}"

    const val SQL_UPDATE_ENTRIES = ""
}