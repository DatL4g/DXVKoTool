package dev.datlag.dxvkotool.ui.compose.fab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FABContainer() {
    Column {
        LoadCacheInfoFAB()
        Spacer(modifier = Modifier.padding(4.dp))
        AddGameFAB()
    }
}
