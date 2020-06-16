package whereismytransport

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import whereismytransport.di.component.ApplicationComponent
import whereismytransport.di.component.DaggerApplicationComponent
import whereismytransport.di.module.ContextModule
import whereismytransport.di.module.ServiceModule
import whereismytransport.whereismycheese.CheesyService


class MyApplication : Application() {

    var component: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        component = DaggerApplicationComponent
                .builder()
                .contextModule(ContextModule(this))
                .serviceModule(ServiceModule(CheesyService()))
                .build()
    }

    fun get(activity: Activity): MyApplication {
        return activity.application as MyApplication
    }

    fun getApplicationComponent(): ApplicationComponent? {
        return component
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "where is my cheese Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "whereIsMyCheese"
    }
}