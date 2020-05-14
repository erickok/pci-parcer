package nl.nl2312.pci.parser.data

data class Publication(
    val shortDoi: String,
    val doi: String,
    val title: String,
    val link: String,
    val description: String,
    var authors: List<Author>? = null
)