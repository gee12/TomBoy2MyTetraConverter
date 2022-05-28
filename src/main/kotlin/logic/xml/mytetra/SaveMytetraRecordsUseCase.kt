package logic.xml.mytetra

import common.*
import model.mytetra.MytetraNode
import model.mytetra.MytetraRecord
import java.io.File
import java.io.FileOutputStream

private const val BASE_FOLDER = "base"

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
        val failures = mutableListOf<Failure>()

        val baseFolder = File(params.mytetraFolder, BASE_FOLDER)
        baseFolder.mkdirs()

        return getRecords(params.nodes).toRight()
            .map { recordsMap ->
                val records = recordsMap.values

                records.forEach { record ->
                    restoreInternalLinks(record, recordsMap)

                    saveRecord(baseFolder, record)
                        .onFailure {
                            failures.add(it)
                        }
                }
            }.map {
                Result(
                    failures = failures
                )
            }
    }

    private fun getRecords(nodes: List<MytetraNode>): Map<String, MytetraRecord> {
        val records = mutableMapOf<String, MytetraRecord>()
        nodes.forEach { node ->
            node.records.forEach { record ->
                records[record.name] = record
            }
            if (node.subNodes.isNotEmpty()) {
                records.putAll(getRecords(node.subNodes))
            }
        }
        return records
    }

    private fun restoreInternalLinks(record: MytetraRecord, records: Map<String, MytetraRecord>) {
        var result = record.content
        "(<a href=\"${Constants.TEMP_INTERNAL_LINK}\">(.+?)</a>)".toRegex().findAll(result).toList().forEach { match ->
            match.groups[2]?.value?.let { recordName ->
                val id = records[recordName]?.id
                if (!id.isNullOrEmpty()) {
                    result = result.replace(match.groups[1]?.value.orEmpty(), "<a href=\"mytetra://note/$id\">$recordName</a>")
                }
            }
        }
        record.content = result
    }

//    private fun saveRecords(baseFolder: File, nodes: List<MytetraNode>): List<Failure> {
//        val failures = mutableListOf<Failure>()
//        for (node in nodes) {
//            if (node.records.isNotEmpty()) {
//                failures.addAll(saveNodeRecords(baseFolder, node.records))
//            }
//        }
//        return failures
//    }
//
//    private fun saveNodeRecords(baseFolder: File, records: List<MytetraRecord>): List<Failure> {
//        val failures = mutableListOf<Failure>()
//        for (record in records) {
//            try {
//                val recordFolder = File(baseFolder, record.dirName)
//                recordFolder.mkdir()
//
//                val recordFile = File(recordFolder, record.fileName)
//                recordFile.createNewFile()
//
//                FileOutputStream(recordFile).use { fos ->
//                    fos.write(record.content.toByteArray())
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//                failures.add(Failure.Mytetra.SaveRecordHtml(ex))
//            }
//        }
//        return failures
//    }

    private fun saveRecord(baseFolder: File, record: MytetraRecord): Either<Failure, None> {
        return try {
            val recordFolder = File(baseFolder, record.dirName)
            recordFolder.mkdir()

            val recordFile = File(recordFolder, record.fileName)
            recordFile.createNewFile()

            FileOutputStream(recordFile).use { fos ->
                fos.write(record.content.toByteArray())
            }
            None.toRight()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Failure.Mytetra.SaveRecordHtml(ex).toLeft()
        }
    }

}