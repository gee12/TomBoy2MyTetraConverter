package model.mytetra

class MytetraNode(
    id: String,
    name: String,
    var level: Int,
    var subNodes: MutableList<MytetraNode> = mutableListOf(),
    var records: MutableList<MytetraRecord> = mutableListOf(),
    var iconName: String? = null,
    var parentNode: MytetraNode? = null
) : MytetraObject(
    id = id,
    name = name
)