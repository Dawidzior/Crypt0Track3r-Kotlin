package dawidzior.crypt0track3r.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dawidzior.crypt0track3r.App
import dawidzior.crypt0track3r.R
import dawidzior.crypt0track3r.details.DetailsActivity
import dawidzior.crypt0track3r.model.CryptoModel

class CryptoItemsListAdapter(private val items: List<CryptoModel>) : RecyclerView.Adapter<CryptoItemsListAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.crypto_list_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crypto: CryptoModel = items[position]
        Picasso.get().load(ICONS_LINK + crypto.symbol.lowercase() + ICON_EXTENSION)
                .placeholder(R.drawable.ic_cloud_download).error(R.drawable.ic_error).into(holder.coinIcon)
        holder.apply {
            coinSymbol.text = crypto.symbol
            coinName.text = crypto.name
            priceUsdText.text = crypto.priceUsd
            percentChange1HText.text = crypto.percentChange1H + PERCENTAGE
            holder.percentChange1HText.setPercentageColor()
            percentChange24HText.text = crypto.percentChange24H + PERCENTAGE
            holder.percentChange24HText.setPercentageColor()
            percentChange7DText.text = crypto.percentChange7D + PERCENTAGE
            holder.percentChange7DText.setPercentageColor()
        }

//    TODO temporary switched off till stable release of the architecture component.
//        holder.itemView.setOnClickListener(
//                Navigation.createNavigateOnClickListener(R.id.action_list_to_details, createBundle(crypto)));
        holder.itemView.setOnClickListener {
            val details = Intent(App.getContext(), DetailsActivity::class.java)
            details.putExtras(createBundle(crypto))
            App.getContext().startActivity(details)
        }
    }


    private fun createBundle(crypto: CryptoModel): Bundle {
        val bundle = Bundle()
        bundle.putString(CRYPTO_NAME, crypto.name)
        bundle.putString(CRYPTO_SYMBOL, crypto.symbol)
        bundle.putString(CRYPTO_PRICE_USD, crypto.priceUsd)
        bundle.putString(CRYPTO_PERC_CHANGE_1H, crypto.percentChange1H + PERCENTAGE)
        bundle.putString(CRYPTO_PERC_CHANGE_24H, crypto.percentChange24H + PERCENTAGE)
        bundle.putString(CRYPTO_PERC_CHANGE_7D, crypto.percentChange7D + PERCENTAGE)
        bundle.putString(CRYPTO_TOTAL_SUPPLY, crypto.totalSupply)
        bundle.putString(CRYPTO_MAX_SUPPLY, crypto.maxSupply)
        bundle.putString(CRYPTO_VOLUME_24H, crypto.volume24H)
        bundle.putString(CRYPTO_MARKET_CAP, crypto.marketCap)
        return bundle
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coinIcon: ImageView = view.findViewById(R.id.coinIcon)
        val coinSymbol: TextView = view.findViewById(R.id.coinSymbol)
        val coinName: TextView = view.findViewById(R.id.coinName)
        val priceUsdText: TextView = view.findViewById(R.id.priceUsdText)
        val percentChange1HText: TextView = view.findViewById(R.id.percentChange1HText)
        val percentChange24HText: TextView = view.findViewById(R.id.percentChange24HText)
        val percentChange7DText: TextView = view.findViewById(R.id.percentChange7DText)
    }

    fun TextView.setPercentageColor() {
        if (this.text[0].toString() == MINUS) this.setTextColor(App.getContext().resources.getColor(R.color.lightRed))
        else this.setTextColor(App.getContext().resources.getColor(R.color.lightGreen))
    }

    companion object {
        const val CRYPTO_NAME = "name"
        const val CRYPTO_SYMBOL = "symbol"
        const val CRYPTO_PRICE_USD = "priceUsd"
        const val CRYPTO_PERC_CHANGE_1H = "percentChange1H"
        const val CRYPTO_PERC_CHANGE_24H = "percentChange24H"
        const val CRYPTO_PERC_CHANGE_7D = "percentChange7D"
        const val CRYPTO_TOTAL_SUPPLY = "totalSupply"
        const val CRYPTO_MAX_SUPPLY = "maxSupply"
        const val CRYPTO_VOLUME_24H = "volume24H"
        const val CRYPTO_MARKET_CAP = "marketCap"
        private const val PERCENTAGE = "%"
        private const val MINUS = "-"
        private const val ICON_EXTENSION = ".png"

        // Icons provided by https://github.com/atomiclabs/cryptocurrency-icons
        private const val ICONS_LINK = "http://res.cloudinary.com/dnpeuogpf/image/upload/cryptos/"


    }

}