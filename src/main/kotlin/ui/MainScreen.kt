import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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

    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .verticalScroll(state = rememberScrollState()),
    ) {

        // tomboy
        Row {
            Button(
                modifier = Modifier,
                content = { Text("Select TomBoy data files") },
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

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                text = "Count: ${tomboyFiles.size}",
                style = TextStyle(
                    fontStyle = FontStyle.Italic
                )
            )

        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // mytetra
        TextField(
            modifier = Modifier,
            label = { Text(text = "Enter MyTetra data folder:") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White
            ),
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .size(16.dp),
                    painter = painterResource("pencil_32.png"),
                    contentDescription = null,
                )
            },
            value = mytetraFolder.orEmpty(),
            onValueChange = { mytetraFolder = it }
        )

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // convert
        Button(
            modifier = Modifier,
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

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // result
        result?.let {
            Text(
                modifier = Modifier,
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
            if (it.mytetraNodes.isNotEmpty()) {
                appendLine()
                appendLine("MyTetra nodes: ${it.mytetraNodes.size}")
                appendLine("MyTetra records: ${it.mytetraNodes.map { it.records }.sumOf { it.size }}")
            }
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
