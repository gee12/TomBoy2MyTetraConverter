package logic.xml.mytetra

import common.*
import model.mytetra.MytetraFile
import model.mytetra.MytetraNode
import model.mytetra.MytetraRecord
import kotlin.Throws
import org.jdom2.output.XMLOutputter
import org.jdom2.DocType
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.output.Format
import org.jdom2.output.LineSeparator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.Exception


private const val MYTETRA_XML = "mytetra.xml"
private const val DATE_TIME_FORMAT = "yyyyMMddHHmmss"
private const val VERSION = "1"
private const val SUBVERSION = "2"

class SaveMytetraXmlUseCase
    : UseCase<UseCase.None, SaveMytetraXmlUseCase.Params>() {

    data class Params(
        val mytetraFolder: File,
        val nodes: List<MytetraNode>
    )

    override suspend fun run(params: Params): Either<Failure, None> {
        return save(params.mytetraFolder, params.nodes)
    }

    private fun save(mytetraFolder: File, nodes: List<MytetraNode>): Either<Failure, None> {
        return try {
            val mytetraXmlFile = File(mytetraFolder, MYTETRA_XML)
            mytetraXmlFile.createNewFile()

            FileOutputStream(mytetraXmlFile).use { fos ->
                // параметры XML
                val format = Format.getPrettyFormat()
                format.encoding = "UTF-8"
                format.indent = " "
                format.setLineSeparator(LineSeparator.UNIX)
                val xmlOutput = XMLOutputter(format)
                val doc = Document()
                doc.docType = DocType("mytetradoc")

                // root
                val rootElem = Element("root")
                doc.rootElement = rootElem

                // format
                val formatElem = Element("format")
                formatElem.setAttribute("version", VERSION)
                formatElem.setAttribute("subversion", SUBVERSION)
                rootElem.addContent(formatElem)

                // content
                val contentElem = Element("content")
                saveNodes(contentElem, nodes)
                rootElem.addContent(contentElem)
                xmlOutput.output(doc, fos)

                None.toRight()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Failure.Mytetra.SaveStorageXml(ex).toLeft()
        }
    }

    /**
     * Сохранение структуры подветок ветки.
     * @param parentElem
     * @param nodes
     * @throws IOException
     */
    @Throws(Exception::class)
    private fun saveNodes(parentElem: Element, nodes: List<MytetraNode>) {
        for (node in nodes) {
            val nodeElem = Element("node")
            val crypted = node.isCrypted
            addAttribute(nodeElem, "crypt", if (crypted) "1" else "")
            addAttribute(nodeElem, "icon", node.iconName)
            addAttribute(nodeElem, "id", node.id)
            addAttribute(nodeElem, "name", node.name)
            if (node.records.isNotEmpty()) {
                saveRecords(nodeElem, node.records)
            }
            if (node.subNodes.isNotEmpty()) {
                saveNodes(nodeElem, node.subNodes)
            }
            parentElem.addContent(nodeElem)
        }
    }

    /**
     * Сохранение структуры записей ветки.
     * @param parentElem
     * @param records
     * @throws IOException
     */
    @Throws(Exception::class)
    private fun saveRecords(parentElem: Element, records: List<MytetraRecord>) {
        val recordsElem = Element("recordtable")
        for (record in records) {
            val recordElem = Element("record")
            val crypted = record.isCrypted
            addAttribute(recordElem, "id", record.id)
            addAttribute(recordElem, "name", record.name)
            addAttribute(recordElem, "author", record.author)
            addAttribute(recordElem, "url", record.url)
            addAttribute(recordElem, "tags", record.tags)
            addAttribute(recordElem, "ctime", record.created.dateToString(DATE_TIME_FORMAT))
            addAttribute(recordElem, "dir", record.dirName)
            addAttribute(recordElem, "file", record.fileName)
            if (crypted) {
                addAttribute(recordElem, "crypt", "1")
            }
            if (record.attachedFiles?.isNotEmpty() == true) {
                saveFiles(recordElem, record.attachedFiles!!)
            }
            recordsElem.addContent(recordElem)
        }
        parentElem.addContent(recordsElem)
    }

    /**
     * Сохранение структуры прикрепленных файлов записи.
     * @param parentElem
     * @param files
     * @throws IOException
     */
    @Throws(Exception::class)
    private fun saveFiles(parentElem: Element, files: List<MytetraFile>) {
        val filesElem = Element("files")
        for (file in files) {
            val fileElem = Element("file")
            val crypted = file.isCrypted
            addAttribute(fileElem, "id", file.id)
            addAttribute(fileElem, "fileName", file.name)
            addAttribute(fileElem, "type", file.fileType)
            if (crypted) {
                addAttribute(fileElem, "crypt", "1")
            }
            filesElem.addContent(fileElem)
        }
        parentElem.addContent(filesElem)
    }

    @Throws(Exception::class)
    private fun addAttribute(elem: Element, name: String, value: String?) {
        elem.setAttribute(name, value.orEmpty())
    }

}