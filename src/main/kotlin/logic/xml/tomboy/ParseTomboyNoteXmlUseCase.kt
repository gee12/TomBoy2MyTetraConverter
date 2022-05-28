package logic.xml.tomboy

import common.*
import model.tomboy.TomboyNote
import org.jdom2.Namespace
import org.jdom2.input.SAXBuilder
import org.jdom2.output.XMLOutputter
import org.jdom2.output.support.AbstractXMLOutputProcessor
import org.jdom2.output.support.FormatStack
import java.io.File
import java.io.IOException
import java.io.Writer


private val NS: Namespace = Namespace.getNamespace("http://beatniksoftware.com/tomboy")
private  const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss" // ISO_8601_24H_FULL_FORMAT

class ParseTomboyNoteXmlUseCase
    : UseCase<TomboyNote, ParseTomboyNoteXmlUseCase.Params>() {

    data class Params(
        val file: File
    )

    override suspend fun run(params: Params): Either<Failure, TomboyNote> {
        return try {
            val sax = SAXBuilder()
            val doc = sax.build(params.file)
            val rootNode = doc.rootElement // <note></note>
            val title = rootNode.getChildText("title", NS) ?: "Без заголовка"
            val created = rootNode.getChildText("create-date", NS).orEmpty()
            val lastChanged = rootNode.getChildText("last-change-date", NS).orEmpty()

            val textNode = rootNode.getChild("text", NS)
            val contentNode = textNode.getChild("note-content", NS)
            val xout = XMLOutputter(NoNamespacesXMLOutputProcessor())
            val content = xout.outputString(contentNode.content).orEmpty()

            val tagsNode = rootNode.getChild("tags", NS)
            val tagsNodes = tagsNode?.getChildren("tag", NS)
            val tags = mutableListOf<String>()
            tagsNodes?.let {
                for (tagNode in tagsNodes) {
                    val tag = tagNode.text?.substring(tagNode.text.lastIndexOf(":") + 1).orEmpty()
                    tags.add(tag)
                }
            }

            TomboyNote(
                title = title,
                content = content,
                created = created.stringToDate(DATE_TIME_FORMAT),
                lastChanged = lastChanged.stringToDate(DATE_TIME_FORMAT),
                tags = tags
            ).toRight()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Failure.Tomboy.ParseNoteXml(ex).toLeft()
        }
    }

    class NoNamespacesXMLOutputProcessor: AbstractXMLOutputProcessor() {
        @Throws(IOException::class)
        override fun printNamespace(
            out: Writer?, fstack: FormatStack?,
            ns: Namespace?
        ) {
            // nothing
        }
    }

}