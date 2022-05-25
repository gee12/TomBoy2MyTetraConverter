package logic.xml.tomboy

import common.*
import model.tomboy.TomboyNote
import java.io.File

class ParseTomboyNotesUseCase
    : UseCase<ParseTomboyNotesUseCase.Result, ParseTomboyNotesUseCase.Params>() {

    data class Params(
        val tomboyFiles: List<File>
    )

    data class Result(
        val tomboyNotes: List<TomboyNote>,
        val failures: List<Failure> = emptyList(),
    )

    private val parseTomboyNoteXmlUseCase = ParseTomboyNoteXmlUseCase()
    private val convertTomboyNoteXmlToHtml = ConvertTomboyNoteXmlToHtml()

    override suspend fun run(params: Params): Either<Failure, Result> {
        val failures = mutableListOf<Failure>()
        val tomboyNotes = mutableListOf<TomboyNote>()

        for (tomboyFile in params.tomboyFiles) {
            val tomboyNote = parseNoteFromXml(tomboyFile, failures) ?: continue

            tomboyNote.content = convertNoteXmlToHtml(tomboyNote, failures) ?: continue

            tomboyNotes.add(tomboyNote)
        }

        return Result(
            failures = failures,
            tomboyNotes = tomboyNotes
        ).toRight()
    }

    private suspend fun parseNoteFromXml(tomboyFile: File, failures: MutableList<Failure>): TomboyNote? {
        return parseTomboyNoteXmlUseCase.run(
            ParseTomboyNoteXmlUseCase.Params(
                file = tomboyFile
            )
        ).foldResult(
            onLeft = {
                failures.add(it)
                null
            },
            onRight = { it }
        )
    }

    private suspend fun convertNoteXmlToHtml(tomboyNote: TomboyNote, failures: MutableList<Failure>): String? {
        return convertTomboyNoteXmlToHtml.run(
            ConvertTomboyNoteXmlToHtml.Params(
                xml = tomboyNote.content
            )
        ).foldResult(
            onLeft = {
                failures.add(it)
                null
            },
            onRight = { it }
        )
    }

}