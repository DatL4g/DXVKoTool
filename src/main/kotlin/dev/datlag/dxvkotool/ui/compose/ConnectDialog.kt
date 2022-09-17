package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.datlag.dxvkotool.model.Node
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ConnectDialog(
    isDialogOpen: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()

    fun closeRequest() {
        coroutineScope.launch(Dispatchers.IO) {
            OnlineDXVK.selectedNodeFlow.emit(null)
        }
        isDialogOpen.value = false
    }

    val structure by OnlineDXVK.dxvkRepoNodeFlow.collectAsState(emptyList())
    val selectedItem: MutableState<Node?> =  remember { mutableStateOf(null) }

    if (isDialogOpen.value) {
        Dialog(onCloseRequest = {
            closeRequest()
        }, title = StringRes.get().connectRepoItem) {
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(8.dp)) {
                Text("Select the matching dxvk-cache info file")

                LazyColumn(
                    modifier = Modifier.weight(1F),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(structure) {
                        ConnectItem(it, selectedItem)
                    }
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = {
                        closeRequest()
                    }) {
                        Text(
                            text = "Close",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(onClick = {
                        println(selectedItem.value)
                    }, enabled = selectedItem.value != null) {
                        Text(
                            text = "Select",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConnectItem(item: Node, selected: MutableState<Node?>) {
    val coroutineScope = rememberCoroutineScope()
    var clickedTwice = false

    Row(modifier = Modifier.onClick {
        if (clickedTwice) {
            if (item.hasChilds()) {
                coroutineScope.launch(Dispatchers.IO) {
                    OnlineDXVK.selectedNodeFlow.emit(item)
                }
            } else {
                selected.value = item
            }
            clickedTwice = false
        } else {
            if (!item.hasChilds()) {
                selected.value = item
            }
            clickedTwice = true
        }
    }) {
        Icon(
            if (item.hasChilds()) Icons.Filled.Folder else Icons.Filled.InsertDriveFile, item.path
        )
        Text(item.path)
    }
}