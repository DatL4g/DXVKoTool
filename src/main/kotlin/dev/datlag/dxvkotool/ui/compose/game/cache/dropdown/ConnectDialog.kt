package dev.datlag.dxvkotool.ui.compose.game.cache.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.datlag.dxvkotool.common.withAlpha
import dev.datlag.dxvkotool.model.github.Node
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Constants
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
    val selectedItem: MutableState<Node?> = remember { mutableStateOf(null) }

    if (isDialogOpen.value) {
        Dialog(onCloseRequest = {
            closeRequest()
        }, title = StringRes.get().connectRepoItem) {
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(8.dp)) {
                Text(StringRes.get().selectMatchingDxvkCache)

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
                            text = StringRes.get().close,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(onClick = {
                        onSelected.invoke(selectedItem.value)
                        closeRequest()
                    }, enabled = selectedItem.value?.hasChilds() == false) {
                        Text(
                            text = StringRes.get().select,
                            color = if (selectedItem.value?.hasChilds() == false) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onBackground.withAlpha(Constants.HALF_ALPHA_NUMBER)
                            }
                        )
                    }
                }
            }
        }
    }
}
