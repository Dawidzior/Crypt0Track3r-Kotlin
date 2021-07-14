package dawidzior.crypt0track3r.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class CryptoModel {
    @PrimaryKey
    @NonNull
    lateinit var id: String

    lateinit var name: String

    lateinit var symbol: String

    @SerializedName("price_usd")
    lateinit var priceUsd: String

    @SerializedName("percent_change_1h")
    lateinit var percentChange1H: String

    @SerializedName("percent_change_24h")
    lateinit var percentChange24H: String

    @SerializedName("percent_change_7d")
    lateinit var percentChange7D: String

    @SerializedName("total_supply")
    lateinit var totalSupply: String

    @SerializedName("max_supply")
    lateinit var maxSupply: String

    @SerializedName("24h_volume_usd")
    lateinit var volume24H: String

    @SerializedName("market_cap_usd")
    lateinit var marketCap: String

    @SerializedName("last_updated")
    lateinit var lastUpdated: String
}