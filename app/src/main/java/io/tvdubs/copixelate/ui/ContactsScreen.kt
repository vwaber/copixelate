package io.tvdubs.copixelate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.R
import io.tvdubs.copixelate.data.Contact
import io.tvdubs.copixelate.viewmodel.TextField
import io.tvdubs.copixelate.viewmodel.UserViewModel

@Composable
fun ContactsScreen(navController: NavController, viewModel: UserViewModel) {
    val contactList by viewModel.contactList.observeAsState(initial = listOf())
    val searchString by viewModel.searchString.observeAsState(initial = "")

    ContactsScreenContent(
        onAddContactClick = { /*TODO*/ },
        searchString = searchString,
        onSearchStringChange = { viewModel.updateTextFieldText(it, TextField.SEARCH_STRING) },
        contactList = contactList,
        clearSearchText = { viewModel.clearTextField() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreenContent(
    onAddContactClick: () -> Unit,
    searchString: String,
    onSearchStringChange: (String) -> Unit,
    contactList: List<Contact>,
    clearSearchText: () -> Unit
) {
    Column {

    }
    Scaffold (
        topBar = {
            TextField(
                value = searchString,
                onValueChange = onSearchStringChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { clearSearchText() }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear Search Text"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddContactClick() }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_person_add_24),
                    contentDescription = "Add contact image."
                )
            }
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            LazyColumn {
                items(contactList) { contact ->
                    ContactCard(contact = contact)
                }
            }
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
       Row(
          modifier = Modifier.fillMaxWidth()
       ) {
           Image(
               painter = painterResource(id = R.drawable._014_09_16__2_),
               contentDescription = "Friend Image",
               modifier = Modifier.padding(4.dp)
           )
           Text(text = contact.username)
       }
    }
}
