package dawidzior.crypt0track3r

import android.app.Application
import android.content.Context
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import dawidzior.crypt0track3r.dependencies.AppModule
import dawidzior.crypt0track3r.dependencies.ApplicationComponent
import dawidzior.crypt0track3r.dependencies.DaggerApplicationComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        appComponent = DaggerApplicationComponent.builder().appModule(AppModule(this)).build()
        analytics = GoogleAnalytics.getInstance(this)
    }

    @Synchronized
    fun getDefaultTracker() = tracker


    companion object {
        lateinit var appComponent: ApplicationComponent
        private lateinit var analytics: GoogleAnalytics
        private lateinit var tracker: Tracker
        private lateinit var app: App

        fun getContext(): Context {
            return app.applicationContext
        }
    }
}

