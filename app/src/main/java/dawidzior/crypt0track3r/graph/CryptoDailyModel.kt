package dawidzior.crypt0track3r.graph

import com.google.gson.annotations.SerializedName

class CryptoDailyModel {
    @SerializedName("TimeTo")
    val time: Long = 0

    @SerializedName("Data")
    val daysList = arrayListOf<DayData>()

}