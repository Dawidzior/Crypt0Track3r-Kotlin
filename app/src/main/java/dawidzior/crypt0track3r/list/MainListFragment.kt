package dawidzior.crypt0track3r.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.firebase.jobdispatcher.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dawidzior.crypt0track3r.AutoUpdateJobService
import dawidzior.crypt0track3r.R
import dawidzior.crypt0track3r.model.CryptoListViewModel
import dawidzior.crypt0track3r.model.CryptoModel


class MainListFragment : Fragment() {
    private lateinit var viewModel: CryptoListViewModel
    private lateinit var dispatcher: FirebaseJobDispatcher

    @BindView(R.id.crypto_list)
    lateinit var cryptoRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_main_list, container, false)
        ButterKnife.bind(this, view)

        val adView: AdView = view.findViewById(R.id.adView)
        val testDeviceIds = arrayListOf(AdRequest.DEVICE_ID_EMULATOR)
        val requestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        adView.loadAd(AdRequest.Builder().build())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO deprecated
        viewModel = ViewModelProviders.of(this).get(CryptoListViewModel::class.java)
        viewModel.apply {
            init()
            getCryptos().observe(viewLifecycleOwner) { cryptos: List<CryptoModel>? -> setCryptosList(cryptos) }
        }
        //TODO deprecated -> WorkManager
        dispatcher = FirebaseJobDispatcher(GooglePlayDriver(activity))
        val autoUpdateJob: Job = dispatcher.newJobBuilder()
                .setService(AutoUpdateJobService::class.java)
                .setTag(AutoUpdateJobService::class.java.simpleName)
                .setRecurring(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT) // According to API documentation, data is refreshed every 5 minutes.
                .setTrigger(Trigger.executionWindow(REFRESH_TIMEOUT_IN_SECONDS, REFRESH_TIMEOUT_IN_SECONDS + 60))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build()
        dispatcher.mustSchedule(autoUpdateJob)
    }

    override fun onPause() {
        super.onPause()
        dispatcher.cancel(AutoUpdateJobService::class.java.simpleName)
    }

    private fun setCryptosList(cryptos: List<CryptoModel>?) {
        cryptos?.let {
            cryptoRecyclerView.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = CryptoItemsListAdapter(cryptos)
            }
        }
    }

    companion object {
        private const val REFRESH_TIMEOUT_IN_SECONDS = 300
    }
}