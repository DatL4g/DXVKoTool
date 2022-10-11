package dev.datlag.dxvkotool.ui.compose.game.cache.dropdown

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.datlag.dxvkotool.common.conditionalBackground
import dev.datlag.dxvkotool.common.withAlpha
import dev.datlag.dxvkotool.model.github.Node
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    }.conditionalBackground(
        isSelected,
        MaterialTheme.colorScheme.onBackground.withAlpha(Constants.HALF_ALPHA_NUMBER),
        MaterialTheme.colorScheme.background
    )) {
        Icon(
            if (item.hasChilds()) Icons.Filled.Folder else Icons.Filled.InsertDriveFile, item.path
        )
        Text(item.path)
    }
}
