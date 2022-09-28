package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CacheInfoDialog(file: MutableState<File?>, isDialogOpen: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val infoCache: MutableState<DxvkStateCache?> = remember { mutableStateOf(null) }
    val infoFile by remember { file }

    if (infoFile != null) {
        coroutineScope.launch(Dispatchers.IO) {
            infoCache.value = DxvkStateCache.fromFile(infoFile!!).getOrNull()
        }
    }

    if (isDialogOpen.value) {
        infoCache.value?.let { cache ->
            AlertDialog(
                modifier = Modifier.defaultMinSize(300.dp),
                onDismissRequest = {
                    infoCache.value = null
                    isDialogOpen.value = false
                },
                title = {
                    Text(
                        text = StringRes.get().cacheInformation,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column {
                        Text(
                            text = cache.file.name,
                            maxLines = 2,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(0.dp, 8.dp)
                        )
                        Text(
                            text = StringRes.get().versionPlaceholder.format(cache.header.version.toString()),
                            maxLines = 1
                        )
                        Text(
                            text = StringRes.get().entriesPlaceholder.format(cache.entries.size),
                            maxLines = 1
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        infoCache.value = null
                        isDialogOpen.value = false
                    }) {
                        Text(
                            text = StringRes.get().close,
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
            )
        }
    }
}
