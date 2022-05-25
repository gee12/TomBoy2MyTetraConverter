import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog
import java.io.File


object Utils {

    fun pickFiles(
        window: ComposeWindow,
        title: String,
        folder: String,
        allowedExtensions: List<String> = emptyList(),
        allowMultiSelection: Boolean = true
    ): Set<File> {
        return FileDialog(
            /* parent = */ window,
            /* title = */ title,
            /* mode = */ FileDialog.LOAD
        ).apply {
            directory = folder
            isMultipleMode = allowMultiSelection

            if (allowedExtensions.isNotEmpty()) {
                // windows
                file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '*.jpg'

                // linux
                setFilenameFilter { _, name ->
                    allowedExtensions.any {
                        name.endsWith(it)
                    }
                }
            }

            isVisible = true
        }.files.toSet()
    }

    fun pickFolder(
        window: ComposeWindow,
        title: String,
        folder: String
    ): File? {
        return FileDialog(
            /* parent = */ window,
            /* title = */ title,
            /* mode = */ FileDialog.LOAD
        ).apply {
            directory = folder
            setFilenameFilter { dir, name ->
                File(dir, name).isDirectory
            }
            isVisible = true
        }.let {
            it.directory?.let { dir ->
                val selectedFolder = File(dir)
                if (selectedFolder.exists() && selectedFolder.isDirectory) {
                    selectedFolder
                } else {
                    null
                }
            }
        }
    }

}