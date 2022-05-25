package model.tomboy

import java.util.*

class TomboyNote(
    var title: String,
    var created: Date?,
    var lastChanged: Date?,
    var content: String,
    val tags: MutableList<String>
)
