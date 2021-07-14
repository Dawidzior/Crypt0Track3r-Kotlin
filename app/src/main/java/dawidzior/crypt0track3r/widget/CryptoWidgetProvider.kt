package dawidzior.crypt0track3r.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import dawidzior.crypt0track3r.App
import dawidzior.crypt0track3r.CryptoModelRepository
import dawidzior.crypt0track3r.R
import dawidzior.crypt0track3r.model.CryptoModel
import dawidzior.crypt0track3r.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CryptoWidgetProvider : AppWidgetProvider() {
    private lateinit var remoteViews: RemoteViews

    private var cryptoModelList: List<CryptoModel>? = null

    @Inject
    lateinit var retrofitService: RetrofitService

    init {
        App.appComponent.inject(this)
    }

    override fun onReceive(context: Context, widgetIntent: Intent) {
        super.onReceive(context, widgetIntent)
        val action = widgetIntent.action
        if (ACTION_NEXT_CRYPTO == action) {
            cryptoNumber++
            if (cryptoNumber == CryptoModelRepository.REQUESTED_COINS) cryptoNumber = 0
            updateTextViews(context)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val call = retrofitService.getCoins(CryptoModelRepository.REQUESTED_COINS)
        call.enqueue(object : Callback<List<CryptoModel>?> {
            override fun onResponse(call: Call<List<CryptoModel>?>, response: Response<List<CryptoModel>?>) {
                cryptoModelList = response.body()
                updateTextViews(context)
            }

            override fun onFailure(call: Call<List<CryptoModel>?>, t: Throwable) {
                Log.d(TAG, "Failure response: " + t.message)
            }
        })
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateTextViews(context: Context) {
        remoteViews = RemoteViews(context.packageName, R.layout.crypto_widget_provider)
        cryptoModelList?.let {
            val intent = Intent(context, CryptoWidgetProvider::class.java)
            intent.action = ACTION_NEXT_CRYPTO
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.apply {
                setTextViewText(R.id.coin_name, cryptoModelList!![cryptoNumber].name)
                setTextViewText(R.id.coin_value, cryptoModelList!![cryptoNumber].priceUsd + DOLLAR)
                setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            }
        } ?: run {
            remoteViews.apply {
                setTextViewText(R.id.coin_name, context.getString(R.string.parsing_data))
                setTextViewText(R.id.coin_value, context.getString(R.string.from_internet))
            }
        }

        // Notify the widget that the list view needs to be updated.
        val mgr = AppWidgetManager.getInstance(context)
        val cn = ComponentName(context, CryptoWidgetProvider::class.java)
        mgr.updateAppWidget(cn, remoteViews)
    }

    companion object {
        private val TAG = CryptoWidgetProvider::class.java.simpleName
        private const val ACTION_NEXT_CRYPTO = "ACTION_NEXT_CRYPTO"
        private const val DOLLAR = "$"
        private var cryptoNumber = 0
    }
}