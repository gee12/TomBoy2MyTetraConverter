package logic

import common.*
import logic.xml.mytetra.SaveMytetraRecordsUseCase
import logic.xml.mytetra.SaveMytetraXmlUseCase
import logic.xml.tomboy.ParseTomboyNotesUseCase
import java.io.File

class ConvertUseCase: UseCase<ConvertUseCase.Result, ConvertUseCase.Params>() {

    data class Params(
        val tomboyFiles: List<File>,
        val mytetraFolder: File
    )

    data class Result(
        val failures: List<Failure> = emptyList(),
    )

    private val parseTomboyNotesUseCase = ParseTomboyNotesUseCase()
    private val tomboyNotesToMytetraNodesConvertUseCase = TomboyNotesToMytetraNodesConvertUseCase()
    private val saveMytetraRecordsUseCase = SaveMytetraRecordsUseCase()
    private val saveMytetraXmlUseCase = SaveMytetraXmlUseCase()

    override suspend fun run(params: Params): Either<Failure, Result> {
        val failures = mutableListOf<Failure>()

        return try {
            parseTomboyNotesUseCase.run(
                ParseTomboyNotesUseCase.Params(
                    tomboyFiles = params.tomboyFiles
                )
            ).flatMap { result ->
                failures.addAll(result.failures)

                tomboyNotesToMytetraNodesConvertUseCase.run(
                    TomboyNotesToMytetraNodesConvertUseCase.Params(
                        tomboyNotes = result.tomboyNotes
                    )
                )
            }.flatMap { mytetraNodes ->
                saveMytetraRecordsUseCase.run(
                    SaveMytetraRecordsUseCase.Params(
                        nodes = mytetraNodes,
                        mytetraFolder = params.mytetraFolder
                    )
                ).flatMap {
                    failures.addAll(it.failures)

                    saveMytetraXmlUseCase.run(
                        SaveMytetraXmlUseCase.Params(
                            mytetraFolder = params.mytetraFolder,
                            nodes = mytetraNodes
                        )
                    ).flatMap {
                        Result(
                            failures = failures
                        ).toRight()
                    }
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            Failure.Convert(ex).toLeft()
        }
    }

}