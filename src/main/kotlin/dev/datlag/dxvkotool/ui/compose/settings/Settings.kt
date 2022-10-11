package dev.datlag.dxvkotool.ui.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigateBack

@Composable
fun Settings() {
    val router = Router.current

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Currently under maintenance",
            color = MaterialTheme.colorScheme.onBackground
        )
        Button(onClick = {
            router.navigateBack()
        }) {
            Text(text = "Back")
        }
    }

}
