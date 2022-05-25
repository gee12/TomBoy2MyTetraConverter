// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

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
        title = "Converter: TomBoy to MyTetra",
        icon = painterResource("icon.png")
    ) {
        App(window = this.window)
    }
}

