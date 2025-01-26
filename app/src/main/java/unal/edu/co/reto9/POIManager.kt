package unal.edu.co.reto9

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class POIManager(private val mapView: MapView) {

    fun fetchPOIs(latitude: Double, longitude: Double, radius: Int, amenity: String) {
        val query = "[out:json];node(around:$radius,$latitude,$longitude)[\"amenity\"=\"$amenity\"];out;"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getPOIs(query)
                if (response.isSuccessful) {
                    val elements = response.body()?.elements ?: emptyList()
                    withContext(Dispatchers.Main) {
                        for (element in elements) {
                            val lat = element.lat
                            val lon = element.lon
                            val name = element.tags?.name ?: "Unknown"
                            val description = element.tags?.description ?: "No description available"
                            val amenity = element.tags?.amenity ?: "No amenity available"
                            val openingHours = element.tags?.opening_hours ?: "No opening hours available"
                            val phone = element.tags?.phone ?: "No phone number available"
                            addMarker(lat, lon, name, description, amenity, openingHours, phone)
                        }
                    }
                } else {
                    println("HTTP request failed: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    private fun addMarker(latitude: Double, longitude: Double, name: String, description: String, amenity: String, openingHours: String, phone: String) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = name
        marker.snippet = description
        marker.subDescription = "Amenity: $amenity\nOpening Hours: $openingHours\nPhone: $phone"
        marker.infoWindow = CustomInfoWindow(mapView)
        mapView.overlays.add(marker)
        mapView.invalidate() // Refresh the map to show the new marker
    }
}
