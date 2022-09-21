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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.datlag.dxvkotool.common.withAlpha
import dev.datlag.dxvkotool.model.Node
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ConnectDialog(
    isDialogOpen: MutableState<Boolean>,
    onSelected: (Node?) -> Unit
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
                        onSelected.invoke(selectedItem.value)
                        closeRequest()
                    }, enabled = selectedItem.value?.hasChilds() == false) {
                        Text(
                            text = "Select",
                            color = if (selectedItem.value?.hasChilds() == false) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.withAlpha(0.5F)
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
    val isSelected = selected.value == item

    Row(modifier = Modifier.onClick {
        if (isSelected) {
            if (item.hasChilds()) {
                coroutineScope.launch(Dispatchers.IO) {
                    OnlineDXVK.selectedNodeFlow.emit(item)
                }
            } else {
                selected.value = item
            }
        } else {
            selected.value = item
        }
    }.background(if (isSelected) MaterialTheme.colorScheme.onBackground.withAlpha(0.5F) else MaterialTheme.colorScheme.background)) {
        Icon(
            if (item.hasChilds()) Icons.Filled.Folder else Icons.Filled.InsertDriveFile, item.path
        )
        Text(item.path)
    }
}