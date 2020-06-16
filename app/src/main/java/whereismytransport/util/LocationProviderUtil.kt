package whereismytransport.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import whereismytransport.di.qualifier.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationProviderUtil @Inject constructor(@ApplicationContext context: Context) {

    @ApplicationContext
    private val mContext: Context = context

    private val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
    private val mutableCurrentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = mutableCurrentLocation

    init {
        getCurrentLocation()
    }

    private fun checkPermission(): Boolean {

        val fineLocationPermission = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return (fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED)
    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            fusedLocation.lastLocation.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocation()
                } else {
                    mutableCurrentLocation.postValue(location)
                }
            }
        }
    }

     fun requestNewLocation() {
        if (checkPermission()) {
            val mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = UPDATE_INTERVAL
            mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL
            mLocationRequest.maxWaitTime = MAX_WAIT_TIME
            fusedLocation.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    Looper.myLooper()
            )
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mutableCurrentLocation.postValue(mLastLocation)
            Log.d("LocationUpdate", "${mLastLocation.latitude} ${mLastLocation.longitude}")
        }
    }

    companion object {
        private val UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(60)
        private val FASTEST_UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(30)
        private val MAX_WAIT_TIME = TimeUnit.MINUTES.toMillis(2)
    }
}