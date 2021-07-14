package dawidzior.crypt0track3r


import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import dawidzior.crypt0track3r.retrofit.RetrofitService
import javax.inject.Inject

class AutoUpdateJobService : JobService() {

    override fun onCreate() {
        super.onCreate()
        App.appComponent.inject(this)
    }

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var cryptoModelRepository: CryptoModelRepository

    override fun onStartJob(job: JobParameters): Boolean {
        Log.d(AutoUpdateJobService::class.java.simpleName, "AutoUpdateJobService onStartJob")
        Thread {
            Log.d(AutoUpdateJobService::class.java.simpleName, "AutoUpdateJobService run")
            cryptoModelRepository.refreshCryptos()
        }.start()
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false
    }

}