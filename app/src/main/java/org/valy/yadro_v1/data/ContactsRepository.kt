package org.valy.yadro_v1.data

import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: ContactsRepository? = null

        fun getInstance(): ContactsRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ContactsRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getContacts(context: Context): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        val selection = "${ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP} = 1"
        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"

        val cursor = context.applicationContext.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            val numberCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            val typeCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)

            while (it.moveToNext()) {
                val type = it.getInt(typeCol)
                if (type != ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) continue

                val id = it.getLong(idCol)
                val name = it.getString(nameCol)
                val number = it.getString(numberCol)
                val photo = it.getString(photoCol)

                contacts.add(Contact(id, name, number, photo))
            }
        }

        contacts.distinctBy { it.id }
    }
}
