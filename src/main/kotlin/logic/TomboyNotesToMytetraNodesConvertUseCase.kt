package logic

import common.Either
import common.Failure
import common.UseCase
import common.toRight
import model.mytetra.MytetraNode
import model.mytetra.MytetraRecord
import model.tomboy.TomboyNote
import java.util.*

private const val ID_SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyz"
private const val UNIQUE_ID_HALF_LENGTH = 10
private const val HTML_START_WITH = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\" \"http://www.w3.org/TR/REC-html40/strict.dtd\">\n" +
        "<html><head>" +
        "<meta name=\"qrichtext\" content=\"1\" />" +
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
        "<style type=\"text/css\">\n" +
        "p, li { white-space: pre-wrap; }\n" +
        "</style></head>" +
        "<body style=\" font-family:'DejaVu Sans'; font-size:11pt; font-weight:400; font-style:normal;\">"
private const val HTML_END_WITH = "</body></html>"
private val ROOT_NODE = MytetraNode(id = "", name = "<root>", level = -1)

class TomboyNotesToMytetraNodesConvertUseCase
    : UseCase<List<MytetraNode>, TomboyNotesToMytetraNodesConvertUseCase.Params>() {

    data class Params(
        val tomboyNotes: List<TomboyNote>
    )

    override suspend fun run(params: Params): Either<Failure, List<MytetraNode>> {
        val nodes = mutableListOf<MytetraNode>()

        for (tomboyNote in params.tomboyNotes) {
            // set node from first tag or root
            val mytetraNode = if (tomboyNote.tags.isNotEmpty()) {
                tomboyNote.tags.first().let { tag ->
                    var node = nodes.firstOrNull { it.name == tag }
                    if (node == null) {
                        // new node
                        node = MytetraNode(
                            id = createUniqueId(),
                            name = tag,
                            level = 0,
                            parentNode = ROOT_NODE
                        )
                        nodes.add(node)
                        ROOT_NODE.subNodes.add(node)
                    }
                    node
                }
            } else {
                ROOT_NODE
            }

            val record = MytetraRecord(
                id = createUniqueId(),
                name = tomboyNote.title,
                node = mytetraNode,
                author = "",
                url = "",
                created = tomboyNote.created ?: Date(),
                dirName = createUniqueId(),
                content = HTML_START_WITH + tomboyNote.content + HTML_END_WITH,
                tags = tomboyNote.tags.joinToString(separator = ", ") { it },
            )
            mytetraNode.records.add(record)
        }

        return nodes.toRight()
    }

    private fun createUniqueId(): String {
        val sb = StringBuilder()
        // 10 digits of (milli)seconds since the UNIX epoch
        val seconds = System.currentTimeMillis().toString()
        val length = seconds.length
        if (length > UNIQUE_ID_HALF_LENGTH) {
            sb.append(seconds.substring(0, UNIQUE_ID_HALF_LENGTH))
        } else if (length < UNIQUE_ID_HALF_LENGTH) {
            sb.append(seconds)
            for (i in 0 until UNIQUE_ID_HALF_LENGTH - length) {
                sb.append('0')
            }
        }
        // 10 random symbols
        val rand = Random()
        for (i in 0 until UNIQUE_ID_HALF_LENGTH) {
            val randIndex = kotlin.math.abs(rand.nextInt()) % ID_SYMBOLS.length
            sb.append(ID_SYMBOLS[randIndex])
        }
        return sb.toString()
    }

}