package whereismytransport.whereismycheese

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import whereismytransport.MyApplication
import whereismytransport.MyApplication.Companion.CHANNEL_ID
import whereismytransport.ui.MainActivity
import whereismytransport.ui.MainViewModel
import whereismytransport.util.CommonUtil.calculateDistance
import whereismytransport.util.LocationProviderUtil
import javax.inject.Inject

/**
 * In order to help the app determine if you are near a cheezy note, you will need to use Location somehow..
 * The idea is that a service will run, constantly checking to see if you have indeed found a cheezy treasure..
 */
class CheesyService() : LifecycleService() {

    @Inject
    lateinit var locationProviderUtil: LocationProviderUtil
    private val cheeseLiveData: LiveData<List<CheesyTreasure>>
        get() = MainViewModel.mutableListOfCheese

    lateinit var myCurrentLocation: Location

    override fun onCreate() {
        super.onCreate()

        (this.application as MyApplication).getApplicationComponent()?.injectCheeseService(this)
        locationProviderUtil.requestNewLocation()
    }

    companion object {

        const val INPUT_EXTRA_CONTENT = "whereismytransport.whereismycheese.content"
        const val INPUT_EXTRA_TITLE = "whereismytransport.whereismycheese.title"

        fun startService(context: Context, title: String, content: String) {
            val startIntent = Intent(context, CheesyService::class.java)
            startIntent.putExtra(INPUT_EXTRA_TITLE, title)
            startIntent.putExtra(INPUT_EXTRA_CONTENT, content)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, CheesyService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        locationProviderUtil.currentLocation.observe(this, Observer { location ->
            location?.let { currentLocation ->
                myCurrentLocation = currentLocation
                getLocationDistance()
            }
        })

        getLocationDistance()
        val content = intent?.getStringExtra(INPUT_EXTRA_CONTENT)
        val title = intent?.getStringExtra(INPUT_EXTRA_TITLE)
        showNotification(title, content)
        return START_NOT_STICKY
    }

    private fun getLocationDistance() {
        cheeseLiveData.observe(this, Observer {
            it?.let { cheeseList ->
                for (cheese in cheeseList) {
                    val distance = calculateDistance(myCurrentLocation, cheese.location!!)
                    if (distance <= 50) {
                        showNotification(getString(R.string.cheese_found_distance, distance.toString()), cheese.note)
                        return@Observer
                    }
                }
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun showNotification(title: String?, content: String?) {
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.cheese32)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }
}