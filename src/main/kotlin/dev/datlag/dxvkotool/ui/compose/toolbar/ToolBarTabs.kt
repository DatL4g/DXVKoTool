package dev.datlag.dxvkotool.ui.compose.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.other.StringRes

@Composable
fun ToolbarTabs(
    selectedIndex: MutableState<Int>
) {
    val list = listOf(StringRes.get().steamGames, StringRes.get().epicGames, StringRes.get().otherGames)

    TabRow(
        selectedTabIndex = selectedIndex.value,
        containerColor = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.fillMaxWidth(),
        indicator = { _ ->
            Box { }
        }
    ) {
        list.forEachIndexed { index, text ->
            val selected = selectedIndex.value == index
            Tab(
                modifier = if (selected) Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onTertiary)
                else Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.tertiary),
                selected = selected,
                onClick = {
                    selectedIndex.value = index
                },
                text = {
                    Text(
                        text = text,
                        color = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onTertiary
                    )
                }
            )
        }
    }
}
