package org.valy.yadro_v1.view.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.valy.yadro_v1.view.components.ContactsList
import org.valy.yadro_v1.viewModel.ContactsViewModel

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel
) {
    val uiState by viewModel.contactsState.collectAsState()
    val context = LocalContext.current

    when (val state = uiState) {
        is ContactsViewModel.ContactsState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ContactsViewModel.ContactsState.PermissionDenied -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Нет доступа к контактам")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) {
                    Text("Открыть настройки")
                }
            }
        }
        is ContactsViewModel.ContactsState.Success -> {
            ContactsList(
                modifier = modifier,
                groupedContacts = state.groupedContacts,
                onContactClick = { number ->
                    context.startActivity(
                        Intent(Intent.ACTION_CALL, "tel:$number".toUri())
                    )
                }
            )
        }
    }
}