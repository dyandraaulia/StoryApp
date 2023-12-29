@file:Suppress("DEPRECATION")

package id.my.storyapp.util

import android.content.Context
import android.location.Geocoder
import java.io.IOException
import java.util.Locale

object LocationFormatter {
    fun getPlaceName(context: Context, lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val locality = address.locality
                val country = address.countryName
                if (locality == null) return country
                return "$locality, $country"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}