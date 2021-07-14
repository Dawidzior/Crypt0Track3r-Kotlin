package dawidzior.crypt0track3r.dependencies

import dagger.Component
import dawidzior.crypt0track3r.AutoUpdateJobService
import dawidzior.crypt0track3r.CryptoModelRepository
import dawidzior.crypt0track3r.model.CryptoListViewModel
import dawidzior.crypt0track3r.widget.CryptoWidgetProvider
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(viewModel: CryptoListViewModel)
    fun inject(repository: CryptoModelRepository)
    fun inject(widgetProvider: CryptoWidgetProvider)
    fun inject(jobService: AutoUpdateJobService)
}
