package com.io.tazarapp.ui.auth.login.city

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import java.util.*

class CityAdapter(private val function: (CityModel?) -> Unit) :
    RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    private var data: MutableList<CityModel> = mutableListOf()
    private var dataCopy: MutableList<CityModel> = mutableListOf()
    var selectedCity: CityModel? = null
    private var checkedItemId: Int? = null
    var update: ((Int?) -> Unit) = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.rv_item_city, parent, false)
        return ViewHolder(view, function)
    }

    fun update(list: MutableList<CityModel>) {
        dataCopy.clear()
        dataCopy.addAll(list)
        this.data = list
        selectedCity = null
        function(selectedCity)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun getCheckedItem() = data.find { city -> city.id == checkedItemId }
    fun setCheckedItemId(id: Int) {
        checkedItemId = id
    }
    fun searchViewCollapsed() {
        data.clear()
        data.addAll(dataCopy)
        selectedCity = null
        function(selectedCity)
        notifyDataSetChanged()
    }

    fun filter(text: String?) {
        data.clear()
        checkedItemId = null
        if (text.isNullOrEmpty()) {
            data.addAll(dataCopy)
        } else {
            for (item in dataCopy) {
                if (item.name.toLowerCase(Locale.getDefault())
                        .contains(text.toLowerCase(Locale.getDefault()), ignoreCase = true)
                ) data.add(item)

            }
        }
        selectedCity = null
        function(selectedCity)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, val function: (CityModel?) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        val hisCode: TextView = itemView.findViewById(R.id.rv_item_tv_city)
        val img: ImageView = itemView.findViewById(R.id.rv_item_img)

        fun bind(item: CityModel) {
            hisCode.text = item.name

            if (selectedCity == item) {
                hisCode.setTextColor(Color.parseColor("#161C25"))
                img.visible()
                function(item)
            } else {
                hisCode.typeface = Typeface.DEFAULT
                hisCode.setTextColor(Color.parseColor("#7B818C"))
                img.gone()
            }
            itemView.apply {
                setOnClickListener {
                    if (selectedCity != item) {
                        selectedCity = item
                        checkedItemId = item.id
                        update.invoke(checkedItemId)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}