package unal.edu.co.reto9

data class OverpassResponse(
    val elements: List<Element>
)

data class Element(
    val lat: Double,
    val lon: Double,
    val tags: Tags?
)

data class Tags(
    val name: String?
)