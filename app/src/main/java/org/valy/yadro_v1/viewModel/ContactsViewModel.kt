package org.valy.yadro_v1.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.valy.yadro_v1.data.Contact
import org.valy.yadro_v1.data.ContactsRepository
import kotlin.collections.groupBy
import kotlin.collections.toSortedMap

class ContactsViewModel : ViewModel() {
    private val contactsRepository: ContactsRepository = ContactsRepository.getInstance()

    private val _contacts = MutableStateFlow(emptyList<Contact>())
    val groupedContacts = _contacts.map { contacts ->
        contacts.groupBy {
            it.name.firstOrNull()?.uppercase() ?: "#"
        }.toSortedMap()
    }

    fun loadContacts(context: Context) {
        viewModelScope.launch {
            _contacts.value = contactsRepository.getContacts(context)
        }
    }
}
