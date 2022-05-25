package model.mytetra

class MytetraFile(
    id: String,
    name: String,
    var fileType: String
) : MytetraObject(
    id = id,
    name = name
)