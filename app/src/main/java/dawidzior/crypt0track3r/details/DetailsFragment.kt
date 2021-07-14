package dawidzior.crypt0track3r.details

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.gson.GsonBuilder
import dawidzior.crypt0track3r.App
import dawidzior.crypt0track3r.R
import dawidzior.crypt0track3r.graph.CryptoDailyModel
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_MARKET_CAP
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_MAX_SUPPLY
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_NAME
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_PERC_CHANGE_1H
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_PERC_CHANGE_24H
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_PERC_CHANGE_7D
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_PRICE_USD
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_SYMBOL
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_TOTAL_SUPPLY
import dawidzior.crypt0track3r.list.CryptoItemsListAdapter.Companion.CRYPTO_VOLUME_24H
import dawidzior.crypt0track3r.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DetailsFragment : Fragment() {

    private lateinit var cryptoCode: String
    private lateinit var tracker: Tracker

    @BindView(R.id.chart)
    lateinit var chartView: CandleStickChart

    @BindView(R.id.coinSymbol)
    lateinit var coinSymbol: TextView

    @BindView(R.id.coinName)
    lateinit var coinName: TextView

    @BindView(R.id.priceUsdText)
    lateinit var priceUsdText: TextView

    @BindView(R.id.percentChange1HText)
    lateinit var percentChange1HText: TextView

    @BindView(R.id.percentChange24HText)
    lateinit var percentChange24HText: TextView

    @BindView(R.id.percentChange7DText)
    lateinit var percentChange7DText: TextView

    @BindView(R.id.totalSupplyText)
    lateinit var totalSupplyText: TextView

    @BindView(R.id.maxSupplyText)
    lateinit var maxSupplyText: TextView

    @BindView(R.id.volume24HText)
    lateinit var volume24HText: TextView

    @BindView(R.id.marketCapText)
    lateinit var marketCapText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application: App = requireActivity().application as App
        tracker = application.getDefaultTracker()
    }

    override fun onResume() {
        super.onResume()
        tracker.apply {
            setScreenName(getString(R.string.details_fragment_title) + coinName.text)
            send(HitBuilders.ScreenViewBuilder().build())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_details, container, false)
        ButterKnife.bind(this, view)
        coinName.text = getString(R.string.name) + requireActivity().intent.extras?.getString(CRYPTO_NAME)
        cryptoCode = requireActivity().intent.extras?.getString(CRYPTO_SYMBOL)!!
        coinSymbol.text = getString(R.string.symbol) + cryptoCode
        priceUsdText.text = getString(R.string.usd_price) + requireActivity().intent.extras?.getString(CRYPTO_PRICE_USD)
        percentChange1HText.text = getString(R.string.hour_change_1) + requireActivity().intent.extras?.getString(CRYPTO_PERC_CHANGE_1H)
        percentChange24HText.text = getString(R.string.hour_change_24) + requireActivity().intent.extras?.getString(CRYPTO_PERC_CHANGE_24H)
        percentChange7DText.text = getString(R.string.days_change_7) + requireActivity().intent.extras?.getString(CRYPTO_PERC_CHANGE_7D)
        totalSupplyText.text = getString(R.string.total_supply) + requireActivity().intent.extras?.getString(CRYPTO_TOTAL_SUPPLY)
        maxSupplyText.text = getString(R.string.max_supply) + requireActivity().intent.extras?.getString(CRYPTO_MAX_SUPPLY)
        volume24HText.text = getString(R.string.volume) + requireActivity().intent.extras?.getString(CRYPTO_VOLUME_24H)
        marketCapText.text = getString(R.string.market_cap) + requireActivity().intent.extras?.getString(CRYPTO_MARKET_CAP)

        val legend: Legend = chartView.legend
        legend.isEnabled = false

        val description: Description = chartView.description
        description.apply {
            textColor = resources.getColor(android.R.color.tertiary_text_dark, requireContext().theme)
            textSize = 18f
            text = getString(R.string.last_30_days)
        }

        chartView.apply {
            setDescription(description)
            setTouchEnabled(false)
            animateY(ANIM_TIME, Easing.EasingOption.Linear)
            xAxis.setDrawLabels(false)
            setNoDataText(getString(R.string.loading_data))
            setNoDataTextColor(resources.getColor(android.R.color.tertiary_text_dark, context.theme))
        }

        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMonthlyCryptoData()
    }

    private fun getMonthlyCryptoData() {
        val retrofit: RetrofitService = Retrofit.Builder().baseUrl(GRAPH_API_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(RetrofitService::class.java)

        if (cryptoCode.isNotEmpty()) {
            if (requireContext().isDeviceOnline()) {
                val retrofitResultCallback: Call<CryptoDailyModel> = retrofit.getHistoryList(cryptoCode, USD)
                retrofitResultCallback.enqueue(object : Callback<CryptoDailyModel?> {
                    override fun onResponse(call: Call<CryptoDailyModel?>, response: Response<CryptoDailyModel?>) {
                        response.body()?.run {
                            val candleEntryList: MutableList<CandleEntry> = ArrayList<CandleEntry>()
                            var index = 0f
                            for (dayData in this.daysList) {
                                candleEntryList.add(CandleEntry(index, dayData.high, dayData.low, dayData.open, dayData.close))
                                index++
                            }
                            val candleDataSet = CandleDataSet(candleEntryList, getString(R.string.last_30_days))
                            candleDataSet.apply {
                                color = Color.GRAY
                                shadowColor = Color.DKGRAY
                                shadowWidth = 1f
                                decreasingColor = resources.getColor(R.color.lightRed, requireContext().theme)
                                decreasingPaintStyle = Paint.Style.FILL
                                increasingColor = resources.getColor(R.color.lightGreen, requireContext().theme)
                                increasingPaintStyle = Paint.Style.FILL
                                neutralColor = Color.BLUE
                                valueTextColor = Color.RED
                                valueTextSize = 0f //hide
                            }
                            val candleData = CandleData(candleDataSet)
                            chartView.apply {
                                data = candleData
                                invalidate()
                            }
                        } ?: { /* TODO zeszła pusta lista, wymagana obsługa przypadku */ }

                    }

                    override fun onFailure(call: Call<CryptoDailyModel?>, t: Throwable) {
                        Log.d(DetailsFragment::class.java.simpleName, "Failure response: " + t.message)
                    }
                })
            }
        } else Toast.makeText(context, R.string.connection_error, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val GRAPH_API_URL = "https://min-api.cryptocompare.com/data/"
        private const val USD = "USD"
        private const val ANIM_TIME = 500
    }
}

fun Context.isDeviceOnline(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    capabilities?.run {
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } ?: return false
}