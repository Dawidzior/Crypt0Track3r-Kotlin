package dawidzior.crypt0track3r.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.core.app.JobIntentService

//TODO JobIntentService jest "deprecated" przerobić na WorkManagewr
class UpdateWidgetService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        val allWidgetIds: IntArray? = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        val clickIntent = Intent(this.applicationContext, CryptoWidgetProvider::class.java)
        clickIntent.apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds)
        }
        stopSelf()
    }
}