package org.valy.yadro_v1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import org.valy.yadro_v1.ui.theme.Yadro_v1Theme
import org.valy.yadro_v1.view.screens.ContactsScreen
import org.valy.yadro_v1.viewModel.ContactsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yadro_v1Theme {
                Scaffold(modifier = Modifier.statusBarsPadding()) { innerPadding ->
                    ContactsScreen(
                        modifier = Modifier.padding(innerPadding),
                        ContactsViewModel()
                    )
                }
            }
        }
    }
}