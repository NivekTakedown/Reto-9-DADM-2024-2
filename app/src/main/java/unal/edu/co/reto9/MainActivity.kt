package unal.edu.co.reto9

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.widget.Button
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity(), RadiusDialogFragment.RadiusDialogListener, POITypeDialogFragment.POITypeDialogListener {

    private lateinit var mapView: MapView
    private val REQUEST_LOCATION_PERMISSION = 1
    private var deviceLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid Configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        setContentView(R.layout.activity_main)

        // Initialize the MapView
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0) // Default zoom level

        // Request permissions and center map on user location
        if (checkLocationPermission()) {
            fetchAndCenterOnUserLocation()
        } else {
            requestLocationPermission()
        }

        // Floating Action Button Click Listener
        val fabSettings = findViewById<FloatingActionButton>(R.id.fabSettings)
        fabSettings.setOnClickListener {
            showRadiusDialog()
        }

        // Button to Center Map on Current Location
        val btnCenterLocation = findViewById<Button>(R.id.btnCenterLocation)
        btnCenterLocation.setOnClickListener {
            if (checkLocationPermission()) {
                fetchAndCenterOnUserLocation()
            } else {
                requestLocationPermission()
            }
        }

        // Button to Select POI Types
        val btnSelectPOITypes = findViewById<Button>(R.id.btnSelectPOITypes)
        btnSelectPOITypes.setOnClickListener {
            showPOITypeDialog()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun fetchAndCenterOnUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatitude = location.latitude
                val userLongitude = location.longitude
                centerMapOnLocation(userLatitude, userLongitude)
                fetchPOIs(userLatitude, userLongitude)
                addDeviceLocationMarker(userLatitude, userLongitude)
            } else {
                Snackbar.make(
                    mapView,
                    "Unable to fetch location. Please try again.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun centerMapOnLocation(latitude: Double, longitude: Double) {
        val userLocation = GeoPoint(latitude, longitude)
        mapView.controller.setCenter(userLocation)
        Snackbar.make(
            mapView,
            "Map centered on your location.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun fetchPOIs(latitude: Double, longitude: Double) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val radius = sharedPreferences.getInt("search_radius", 1000)
        val poiTypes = sharedPreferences.getStringSet("poi_types", setOf("restaurant")) ?: setOf("restaurant")
        val poiManager = POIManager(mapView)
        for (type in poiTypes) {
            poiManager.fetchPOIs(latitude, longitude, radius, type)
        }
    }

    private fun showRadiusDialog() {
        val dialog = RadiusDialogFragment()
        dialog.show(supportFragmentManager, "RadiusDialogFragment")
    }

    private fun showPOITypeDialog() {
        val dialog = POITypeDialogFragment()
        dialog.show(supportFragmentManager, "POITypeDialogFragment")
    }

    private fun addDeviceLocationMarker(latitude: Double, longitude: Double) {
        if (deviceLocationMarker == null) {
            deviceLocationMarker = Marker(mapView)
            deviceLocationMarker?.icon = ContextCompat.getDrawable(this, R.drawable.ic_device_location)
            deviceLocationMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(deviceLocationMarker)
        }
        deviceLocationMarker?.position = GeoPoint(latitude, longitude)
        mapView.invalidate() // Refresh the map to show the new marker
    }

    override fun onRadiusUpdated() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchPOIs(location.latitude, location.longitude)
            }
        }
    }

    override fun onPOITypesUpdated() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchPOIs(location.latitude, location.longitude)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            fetchAndCenterOnUserLocation()
        } else {
            Snackbar.make(
                mapView,
                "Location permission is required to center the map.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Needed for OSMDroid
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Needed for OSMDroid
    }
}