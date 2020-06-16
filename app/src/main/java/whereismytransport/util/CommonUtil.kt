package whereismytransport.util

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import com.mapbox.mapboxsdk.geometry.LatLng


object CommonUtil {

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun calculateDistance(startLocation: Location, endLocation: LatLng): Float {
        val endPoint = Location("End")
        endPoint.longitude = endLocation.longitude
        endPoint.latitude = endLocation.latitude
        return startLocation.distanceTo(endPoint)
    }
}