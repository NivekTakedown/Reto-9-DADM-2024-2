package unal.edu.co.reto9

import android.view.View
import android.widget.Button
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.Marker

class CustomInfoWindow(mapView: MapView) : InfoWindow(R.layout.info_window, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as Marker
        val title = mView.findViewById<TextView>(R.id.title)
        val description = mView.findViewById<TextView>(R.id.description)
        val amenity = mView.findViewById<TextView>(R.id.amenity)
        val openingHours = mView.findViewById<TextView>(R.id.opening_hours)
        val phone = mView.findViewById<TextView>(R.id.phone)
        val closeButton = mView.findViewById<Button>(R.id.close_button)

        title.text = marker.title
        description.text = marker.snippet
        val subDescription = marker.subDescription?.split("\n")
        amenity.text = subDescription?.getOrNull(0) ?: "No amenity available"
        openingHours.text = subDescription?.getOrNull(1) ?: "No opening hours available"
        phone.text = subDescription?.getOrNull(2) ?: "No phone number available"

        closeButton.setOnClickListener {
            close()
        }
    }

    override fun onClose() {
        // Do nothing
    }
}