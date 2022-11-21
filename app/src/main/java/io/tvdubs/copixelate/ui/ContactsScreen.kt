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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import io.tvdubs.copixelate.R
import io.tvdubs.copixelate.data.Contact
import io.tvdubs.copixelate.viewmodel.TextField
import io.tvdubs.copixelate.viewmodel.UserViewModel

@Composable
fun ContactsScreen(navController: NavController, viewModel: UserViewModel) {
    val contactList by viewModel.contactList.observeAsState(initial = listOf())
    val searchString by viewModel.searchString.observeAsState(initial = "")
    val showDialog by viewModel.showDialog.observeAsState(initial = false)
    val addContactString by viewModel.addContactString.observeAsState(initial = "")
    val context = LocalContext.current

    // ToDo: Contact list isn't refreshing when new contact added.
    // ToDo: Toast for success or failure not showing.
    
    ContactsScreenContent(
        onAddContactClick = { viewModel.changeDialogState() },
        searchString = searchString,
        onSearchStringChange = { viewModel.updateTextFieldText(it, TextField.SEARCH_STRING) },
        contactList = contactList,
        clearSearchText = { viewModel.clearTextField() },
        onContactCardClick = { /*ToDo: Create artboard and send id to database locations*/ },
        showDialog = showDialog,
        addContactString = addContactString,
        onAddContactStringChange = { viewModel.updateTextFieldText(it, TextField.ADD_CONTACT_STRING) },
        onAddContactButtonClick = {
            viewModel.addContact(it, context)
            viewModel.changeDialogState()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreenContent(
    onAddContactClick: () -> Unit,
    searchString: String,
    onSearchStringChange: (String) -> Unit,
    contactList: List<Contact>,
    clearSearchText: () -> Unit,
    onContactCardClick: () -> Unit,
    showDialog: Boolean,
    addContactString: String,
    onAddContactStringChange: (String) -> Unit,
    onAddContactButtonClick: (String) -> Unit
) {
    if (showDialog) {
      AddContactDialog(onAddContactClick, addContactString, onAddContactStringChange, onAddContactButtonClick)
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
                    ContactCard(contact = contact, cardClick = { onContactCardClick() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCard(contact: Contact, cardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        onClick = { cardClick() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    dismiss: () -> Unit,
    contactEntryText: String,
    onContactEntryTextChange: (String) -> Unit,
    onAddContactClick: (String) -> Unit
) {
    Dialog(onDismissRequest = { dismiss() }) {
        Surface(
            color = Color.White
        ) {
            Column {
                Text(text = "Add User", modifier = Modifier.padding(all = 16.dp))
                OutlinedTextField(
                    value = contactEntryText,
                    onValueChange = onContactEntryTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    placeholder = { Text(text = "Username") },
                )
                Button(
                    modifier = Modifier.padding(all = 16.dp),
                    onClick = { onAddContactClick(contactEntryText) }
                ) {
                    Text(text = "Add Contact")
                }
            }
        }
    }
}
