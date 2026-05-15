package org.valy.yadro_v1.view.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.valy.yadro_v1.data.Contact
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ContactsList(
    modifier: Modifier,
    groupedContacts: Map<String, List<Contact>>,
    onContactClick: (String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        groupedContacts.forEach { (letter, contacts) ->
            item(key = "header_$letter") {
                SectionHeader(letter = letter)
            }
            items(contacts, key = { it.id }) { contact ->
                ContactListItem(
                    contact = contact,
                    onItemClick = onContactClick
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun SectionHeader(letter: String) {
    Text(
        text = letter,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}