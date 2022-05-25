package logic.xml.mytetra

import common.Either
import common.Failure
import common.UseCase
import common.toRight
import model.mytetra.MytetraNode
import model.mytetra.MytetraRecord
import java.io.File
import java.io.FileOutputStream

class SaveMytetraRecordsUseCase
    : UseCase<SaveMytetraRecordsUseCase.Result, SaveMytetraRecordsUseCase.Params>() {

    data class Params(
        val mytetraFolder: File,
        val nodes: List<MytetraNode>
    )

    data class Result(
        val failures: List<Failure> = mutableListOf()
    )

    override suspend fun run(params: Params): Either<Failure, Result> {
        val baseFolder = File(params.mytetraFolder, "base")
        baseFolder.mkdirs()
        return Result(
            failures = saveRecords(baseFolder, params.nodes)
        ).toRight()

    }

    private fun saveRecords(baseFolder: File, nodes: List<MytetraNode>): List<Failure> {
        val failures = mutableListOf<Failure>()
        for (node in nodes) {
            if (node.records.isNotEmpty()) {
                failures.addAll(saveNodeRecords(baseFolder, node.records))
            }
        }
        return failures
    }

    private fun saveNodeRecords(baseFolder: File, records: List<MytetraRecord>): List<Failure> {
        val failures = mutableListOf<Failure>()
        for (record in records) {
            try {
                val recordFolder = File(baseFolder, record.dirName)
                recordFolder.mkdir()

                val recordFile = File(recordFolder, record.fileName)
                recordFile.createNewFile()

                FileOutputStream(recordFile).use { fos ->
                    fos.write(record.content.toByteArray())
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                failures.add(Failure.Mytetra.SaveRecordHtml(ex))
            }
        }
        return failures
    }

}