package unal.edu.co.reto9

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class RadiusDialogFragment : DialogFragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val radiusInput = TextInputEditText(requireContext())
        radiusInput.hint = "Enter radius in meters"
        radiusInput.setText(sharedPreferences.getInt("search_radius", 1000).toString())

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Set Search Radius")
            .setView(radiusInput)
            .setPositiveButton("Save") { _, _ ->
                val radius = radiusInput.text.toString().toIntOrNull() ?: 1000
                sharedPreferences.edit().putInt("search_radius", radius).apply()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}