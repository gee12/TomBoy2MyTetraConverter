package logic.xml.tomboy

import common.Either
import common.Failure
import common.UseCase
import common.toRight


class ConvertTomboyNoteXmlToHtml
    : UseCase<String, ConvertTomboyNoteXmlToHtml.Params>() {

    data class Params(
        val xml: String
    )

    override suspend fun run(params: Params): Either<Failure, String> {
        var result = params.xml

        // make note body a one liner
        result = result.replace("\r\n", "<BR/>")
        result = result.replace("\n", "<BR/>")

        // links
        result = result.replace("<link:internal>(.+?)</link:internal>".toRegex()) { "${it.groups[1]?.value}" }
        result = result.replace("<link:broken>(.+?)</link:broken>".toRegex()) { "${it.groups[1]?.value}" }

        "(<link:url>(.+?)</link:url>)".toRegex().findAll(result).toList().forEach { match ->
            result = if ("^([a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,10}|https?://.+)$".toRegex().matches(match.groups[2]?.value.orEmpty())) {
                result.replace(match.groups[1]?.value.orEmpty(), "<a href=\"${match.groups[2]}\">${match.groups[2]}</a>")
            } else {
                result.replace(match.groups[1]?.value.orEmpty(), match.groups[2]?.value.orEmpty())
            }
        }

        // lists
        result = result.replace("<BR/><list>", "<list>") // redundant indent before list
        result = result.replace("</list><BR/>", "</list>") // redundant indent after list
        result = result.replace("</list>(<BR/>)?<list>".toRegex()) { "\n" } // redundant start new list
        result = result.replace("<(/?)list>".toRegex()) { "<${it.groups[1]?.value}ul>" }
        result = result.replace("<list-item dir=\"ltr\">", "<li>")
        result = result.replace("<(/?)list-item>".toRegex()) { "<${it.groups[1]?.value}li>" }

        // higlight
        result = result.replace("<highlight>(.+?)</highlight>".toRegex()) { "<span style=\"background:yellow\">${it.groups[1]?.value}</span>" }

        // font size
        result = result.replace("<size:small>(.+?)</size:small>".toRegex()) { "<span style=\"font-size:small\">${it.groups[1]?.value}</span>" }
        result = result.replace("<size:large>(.+?)</size:large>".toRegex()) { "<span style=\"font-size:large\">${it.groups[1]?.value}</span>" }
        result = result.replace("<size:huge>(.+?)</size:huge>".toRegex()) { "<span style=\"font-size:xx-large\">${it.groups[1]?.value}</span>" }

        // text style
        result = result.replace("<(/?)monospace>".toRegex()) { "<${it.groups[1]?.value}code>" }
        result = result.replace("<(/?)bold>".toRegex()) { "<${it.groups[1]?.value}b>" }
        result = result.replace("<(/?)italic>".toRegex()) { "<${it.groups[1]?.value}i>" }
        //result = result.replace("<(/?)strikeout>".toRegex()) { "<${it.groups[1]?.value}strike>" }
        result = result.replace("<strikeout>", "<span style=\" text-decoration: line-through;\">")
        result = result.replace("</strikeout>", "</span>")
        result = result.replace("<(/?)underline>".toRegex()) { "<${it.groups[1]?.value}u>" }

        // indentation
        result = result.replace("\t", "&nbsp; &nbsp; &nbsp; &nbsp;")

        // set new lines
        result = result.replace("<BR/>", "<br/>\n")

        return result.toRight()
    }

}