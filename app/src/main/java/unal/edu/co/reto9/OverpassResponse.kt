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
    val name: String?,
    val description: String?, // Existing field
    val amenity: String?, // New field
    val opening_hours: String?, // New field
    val phone: String? // New field
)