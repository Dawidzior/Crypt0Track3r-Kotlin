package dawidzior.crypt0track3r.dependencies

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dawidzior.crypt0track3r.retrofit.RetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {

    private val apiUrl = "https://api.coinmarketcap.com/v1/"

    @Provides
    @Singleton
    fun getRetrofitInstance(): RetrofitService = Retrofit.Builder().baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(RetrofitService::class.java)

}