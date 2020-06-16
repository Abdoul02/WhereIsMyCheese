package whereismytransport.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import whereismytransport.MyApplication
import whereismytransport.util.CommonUtil.isLocationEnabled
import whereismytransport.util.CommonUtil.showToast
import whereismytransport.whereismycheese.CheesyDialog
import whereismytransport.whereismycheese.CheesyDialog.INoteDialogListener
import whereismytransport.whereismycheese.CheesyService
import whereismytransport.whereismycheese.CheesyTreasure
import whereismytransport.whereismycheese.Constants.PERMISSIONS
import whereismytransport.whereismycheese.R
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainViewModel: MainViewModel
    lateinit var currentPoint: LatLng
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    public override fun onDestroy() {
        super.onDestroy()
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (this.application as MyApplication).getApplicationComponent()?.injectMainActivity(this)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)


        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        mapView = findViewById<View>(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)


        // One does not simply just cheez, you require some permissions.
        val fineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        val backGroundLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED && backGroundLocation == PackageManager.PERMISSION_GRANTED
                && coarseLocation == PackageManager.PERMISSION_GRANTED) {
            initializeMap()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSIONS.Companion.ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS.ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    initializeMap()
                } else {
                    this.finish()
                    exitProcess(0)
                }
                return
            }
        }
    }

    private fun initializeMap() {
        if (isLocationEnabled(this)) {
            startService()
            mapView.getMapAsync { mapboxMap ->
                map = mapboxMap
                map.setStyle(Style.MAPBOX_STREETS)
                setupLongPressListener()
                markerClickListener()
                populateMap()
                mainViewModel.currentLocation.observe(this, Observer {
                    it.let { location ->
                        currentPoint = LatLng(location.latitude, location.longitude)
                        addCheeseToMap(currentPoint, getString(R.string.current_location), true)
                        val position = CameraPosition.Builder()
                                .target(currentPoint)
                                .zoom(17.0)
                                .bearing(180.0)
                                .tilt(30.0)
                                .build()
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000)
                    }
                })
            }
        } else {
            showToast(this, getString(R.string.turn_notification))
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun setupLongPressListener() {
        map.setOnMapLongClickListener { point -> createCheesyNote(point) }
    }

    private fun markerClickListener() {
        map.setOnMarkerClickListener { marker ->

            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.cheese_found))
            builder.setMessage(getString(R.string.collect_cheese))
            builder.setPositiveButton(R.string.yes) { dialog, which ->
                val cheesyTreasure = CheesyTreasure(marker.position, marker.title)
                mainViewModel.removeCheese(cheesyTreasure)
                showToast(this, getString(R.string.cheese_collected))
                map.removeMarker(marker)
            }

            builder.setNegativeButton(R.string.no) { dialog, which ->
                showToast(this, getString(R.string.cheese_rejected))
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        }
    }

    private fun populateMap() {
        MainViewModel.mutableListOfCheese.observe(this, Observer {
            it?.let { list ->
                for (cheese in list) {
                    addCheeseToMap(cheese.location!!, cheese.note!!)
                }
            }
        })
    }

    private fun createCheesyNote(point: LatLng) {
        val note = CheesyDialog(this, object : INoteDialogListener {
            override fun onNoteAdded(note: String) {
                val cheesyTreasure = CheesyTreasure(point, note)
                mainViewModel.addCheese(cheesyTreasure)
            }
        })
        note.show()
    }

    private fun startService() {
        CheesyService.startService(this, getString(R.string.searching), "")
    }

    private fun addCheeseToMap(point: LatLng, content: String, currentLocation: Boolean = false) {
        val iconFactory = IconFactory.getInstance(this@MainActivity)
        val icon = if (currentLocation) iconFactory.fromBitmap(getBitmapFromDrawableId(R.drawable.ic_location)) else
            iconFactory.fromBitmap(getBitmapFromDrawableId(R.drawable.cheese64))
        val marker = MarkerOptions()
        marker.icon = icon
        marker.position = point
        marker.title = content
        map.addMarker(marker)
        // markers.add(map.addMarker(marker))
    }

    private fun getBitmapFromDrawableId(drawableId: Int): Bitmap {
        val vectorDrawable: Drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(drawableId, null)
        } else {
            resources.getDrawable(drawableId)
        }
        val wrapDrawable = DrawableCompat.wrap(vectorDrawable)
        var h = vectorDrawable.intrinsicHeight
        var w = vectorDrawable.intrinsicWidth
        h = if (h > 0) h else 96
        w = if (w > 0) w else 96
        wrapDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        wrapDrawable.draw(canvas)
        return bm
    }
}