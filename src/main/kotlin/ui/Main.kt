// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.gee__.TeraBoy_Mytetra.BuildConfig

@Composable
@Preview
fun App(window: ComposeWindow) {
    MaterialTheme {
        MainScreen(window)
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Data converter: TomBoy to MyTetra (${BuildConfig.APP_VERSION})",
        icon = painterResource("icon.png"),
        state = WindowState(size = DpSize(640.dp, 480.dp))
    ) {
        App(window = this.window)
    }
}

