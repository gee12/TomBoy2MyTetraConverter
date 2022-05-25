package model.mytetra

import java.util.Date

class MytetraRecord(
    id: String,
    name: String,
    var node: MytetraNode,
    var tags: String,
    var author: String,
    var url: String,
    var created: Date,
    var dirName: String,
    var fileName: String = "text.html",
    var content: String,
    var attachedFiles: MutableList<MytetraFile>? = null
) : MytetraObject(
    id = id,
    name = name
)