package org.valy.yadro_v1

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import org.valy.yadro_v1.view.screens.ContactsScreen
import org.valy.yadro_v1.viewModel.ContactsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ContactsViewModel by viewModels()

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.READ_CONTACTS] == true) {
            viewModel.loadContacts(this)
        } else {
            viewModel.onContactsPermissionDenied()
        }

        if (permissions[Manifest.permission.CALL_PHONE] == true) {
            viewModel.onCallPermissionGranted()
        } else {
            viewModel.onCallPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions.launch(
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE
            )
        )

        setContent {
            ContactsScreen(viewModel = viewModel)
        }
    }
}