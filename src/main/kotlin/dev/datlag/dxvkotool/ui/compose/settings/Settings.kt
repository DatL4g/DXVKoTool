package dev.datlag.dxvkotool.ui.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigateBack
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.ui.compose.toolbar.SettingsToolBar

@Composable
fun Settings() {
    val router = Router.current
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    CompositionLocalProvider(LocalSnackbarHost provides scaffoldState.snackbarHostState) {
        Scaffold(
            topBar = {
                SettingsToolBar()
            },
            scaffoldState = scaffoldState
        ) {
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
    }
}
