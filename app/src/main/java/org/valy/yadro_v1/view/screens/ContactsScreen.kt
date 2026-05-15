package org.valy.yadro_v1.view.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.valy.yadro_v1.view.components.ContactsList
import org.valy.yadro_v1.viewModel.ContactsViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel
) {
    val groupedContacts by viewModel.groupedContacts.collectAsState(initial = sortedMapOf())
    val context = LocalContext.current

    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)
    val contactsPermissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)

    var pendingPhoneNumber by remember { mutableStateOf<String?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contactsPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(contactsPermissionState.status) {
        when {
            contactsPermissionState.status.isGranted -> {
                viewModel.loadContacts(context)
                callPermissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(callPermissionState.status.isGranted) {
        val number = pendingPhoneNumber ?: return@LaunchedEffect
        if (callPermissionState.status.isGranted) {
            context.startActivity(Intent(Intent.ACTION_CALL, "tel:$number".toUri()))
            pendingPhoneNumber = null
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Требуется разрешение") },
            text = { Text("Предоставьте разрешение на совершение звонков в настройках приложения") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) { Text("Открыть настройки") }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) { Text("Отмена") }
            }
        )
    }

    when {
        contactsPermissionState.status.isGranted -> {
            ContactsList(
                modifier = modifier,
                groupedContacts = groupedContacts,
                onContactClick = { number ->
                    when {
                        callPermissionState.status.isGranted -> {
                            context.startActivity(
                                Intent(Intent.ACTION_CALL, "tel:$number".toUri())
                            )
                        }
                        callPermissionState.status.shouldShowRationale -> {
                            pendingPhoneNumber = number
                            callPermissionState.launchPermissionRequest()
                        }
                        else -> {
                            showSettingsDialog = true
                        }
                    }
                }
            )
        }

        contactsPermissionState.status.shouldShowRationale -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { contactsPermissionState.launchPermissionRequest() }) {
                    Text("Открыть доступ к контактам")
                }
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TextButton(onClick = {
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
    }
}
