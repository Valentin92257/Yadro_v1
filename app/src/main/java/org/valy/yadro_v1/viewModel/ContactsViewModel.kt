package org.valy.yadro_v1.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.valy.yadro_v1.data.Contact
import org.valy.yadro_v1.data.ContactsRepository
import java.util.SortedMap

class ContactsViewModel : ViewModel() {

    private val repository = ContactsRepository.getInstance()

    sealed class ContactsState {
        object Loading : ContactsState()
        object PermissionDenied : ContactsState()
        data class Success(val groupedContacts: SortedMap<String, List<Contact>>) : ContactsState()
    }

    private val _contactsState = MutableStateFlow<ContactsState>(ContactsState.Loading)
    val contactsState: StateFlow<ContactsState> = _contactsState.asStateFlow()

    private val _callPermissionGranted = MutableStateFlow(false)
    val callPermissionGranted: StateFlow<Boolean> = _callPermissionGranted.asStateFlow()

    fun loadContacts(context: Context) {
        viewModelScope.launch {
            val grouped = repository.getContacts(context)
                .groupBy { it.name.firstOrNull()?.uppercase() ?: "#" }
                .toSortedMap()
            _contactsState.value = ContactsState.Success(grouped)
        }
    }

    fun onContactsPermissionDenied() {
        _contactsState.value = ContactsState.PermissionDenied
    }

    fun onCallPermissionGranted() {
        _callPermissionGranted.value = true
    }

    fun onCallPermissionDenied() {
        _callPermissionGranted.value = false
    }
}