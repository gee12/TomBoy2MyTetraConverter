import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import java.io.File
import common.Either
import common.Failure
import common.map
import kotlinx.coroutines.launch
import logic.ConvertUseCase


@Composable
fun MainScreen(window: ComposeWindow) {

    val convertUseCase = remember { ConvertUseCase() }
    var result: Either<Failure, ConvertUseCase.Result>? by remember { mutableStateOf(null) }
    var tomboyFiles by remember { mutableStateOf(emptyList<File>()) }
    var mytetraFolder by remember { mutableStateOf(System.getProperty("compose.application.resources.dir")) }
    val coroutineScope = rememberCoroutineScope()

    Column {

        // tomboy
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                text = "TomBoy data files"
            )

            Button(
                modifier = Modifier
                    .padding(start = 16.dp),
                content = { Text("select") },
                onClick = {
                    Utils.pickFiles(
                        window = window,
                        title = "Select TomBoy data files",
                        folder = tomboyFiles.firstOrNull()?.parent.orEmpty(),
                        allowMultiSelection = true
                    ).let {
                        tomboyFiles = it.toList()
                    }
                }
            )
        }

        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = "Count: ${tomboyFiles.size}",
            style = TextStyle(
                fontStyle = FontStyle.Italic
            )
        )

        // mytetra
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                text = "MyTetra data folder"
            )

//            Button(
//                content = { Text("select") },
//                onClick = {
//                    Utils.pickFolder(
//                        window = window,
//                        title = "Select MyTetra data folder",
//                        folder = mytetraFolder.absolutePath.orEmpty()
//                    )?.let {
//                        mytetraFolder = it
//                    }
//                }
//            )
            TextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                value = mytetraFolder.orEmpty(),
                onValueChange = { mytetraFolder = it }
            )
        }

        // convert
        Button(
            modifier = Modifier
                .padding(16.dp),
            content = { Text("convert") },
            enabled = tomboyFiles.isNotEmpty() && mytetraFolder.isNotEmpty(),
            onClick = {
                coroutineScope.launch {
                    result = convertUseCase.run(
                        ConvertUseCase.Params(
                            tomboyFiles = tomboyFiles,
                            mytetraFolder = File(mytetraFolder)
                        )
                    )
                }
            }
        )

        result?.let {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = getResultText(it),
                style = TextStyle(
                    fontStyle = FontStyle.Italic
                )
            )
        }

    }
}

private fun getResultText(result: Either<Failure, ConvertUseCase.Result>): String {
    return result.map {
        StringBuilder().apply {
            appendLine("Converted successfully")
            if (it.failures.isNotEmpty()) {
                appendLine()
                appendLine("Also has errors:")
                appendLine(it.failures.joinToString(separator = "\n\n") { failure ->
                    failure.exception?.message.orEmpty()
                })
            }
        }.toString()
    }.foldResult(
        onLeft = { failure ->
            "Convert failed" + failure.exception?.let { ex -> "\nError: $ex" }.orEmpty()
        },
        onRight = { it }
    )
}
