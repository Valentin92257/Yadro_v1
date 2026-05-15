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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import org.valy.yadro_v1.view.components.ContactsList
import org.valy.yadro_v1.viewModel.ContactsViewModel

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel
) {
    val contactsState by viewModel.contactsState.collectAsState()
    val callPermissionGranted by viewModel.callPermissionGranted.collectAsState()
    val context = LocalContext.current
    var showCallPermissionDialog by remember { mutableStateOf(false) }

    if (showCallPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showCallPermissionDialog = false },
            title = { Text("Требуется разрешение на звонки") },
            text = { Text("Чтобы звонить контактам, разрешите доступ к телефону в настройках") },
            confirmButton = {
                TextButton(onClick = {
                    showCallPermissionDialog = false
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) { Text("Открыть настройки") }
            },
            dismissButton = {
                TextButton(onClick = { showCallPermissionDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(modifier = modifier) { paddingValues ->
        when (val state = contactsState) {
            is ContactsViewModel.ContactsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ContactsViewModel.ContactsState.PermissionDenied -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Нет доступа к контактам",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp
                        ),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }) {
                        Text(
                            text = "Открыть настройки",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                fontSize = 20.sp
                            ),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            is ContactsViewModel.ContactsState.Success -> {
                if (state.groupedContacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Контакты не найдены",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            ),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                } else {
                    ContactsList(
                        modifier = Modifier.padding(paddingValues),
                        groupedContacts = state.groupedContacts,
                        onContactClick = { number ->
                            if (callPermissionGranted) {
                                context.startActivity(
                                    Intent(Intent.ACTION_CALL, "tel:$number".toUri())
                                )
                            } else {
                                showCallPermissionDialog = true
                            }
                        }
                    )
                }
            }
        }
    }
}