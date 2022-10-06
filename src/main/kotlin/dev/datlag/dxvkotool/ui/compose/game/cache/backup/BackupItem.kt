package dev.datlag.dxvkotool.ui.compose.game.cache.backup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import dev.datlag.dxvkotool.common.conditionalBackground
import dev.datlag.dxvkotool.common.getLastModifiedOrCreated
import dev.datlag.dxvkotool.common.sizeSafely
import dev.datlag.dxvkotool.common.toHumanReadableBytes
import dev.datlag.dxvkotool.common.withAlpha
import dev.datlag.dxvkotool.other.Constants
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackupItem(
    file: File,
    selected: MutableState<File?>
) {
    val lastModified = Instant.fromEpochMilliseconds(file.getLastModifiedOrCreated())
    val lastModifiedDate = lastModified.toLocalDateTime(TimeZone.currentSystemDefault())
    val isSelected = selected.value == file

    Row(
        modifier = Modifier.fillMaxWidth().onClick {
            selected.value = file
        }.conditionalBackground(
            isSelected,
            MaterialTheme.colorScheme.onBackground.withAlpha(Constants.HALF_ALPHA_NUMBER),
            MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            modifier = Modifier.weight(2F),
            text = lastModifiedDate.toJavaLocalDateTime().format(Constants.defaultDateFormatter)
        )
        Text(
            modifier = Modifier.weight(1F),
            text = file.sizeSafely().toHumanReadableBytes()
        )
    }
}
