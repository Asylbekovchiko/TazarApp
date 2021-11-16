package com.io.tazarapp.ui.citizen.ad.adCreate.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.City
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.city_list_item.view.*

class Step4Adapter : RecyclerView.Adapter<Step4Adapter.CityViewHolder>() {
    private var checkedItemId: Int? = null
    private var cities = ArrayList<City>()
    private var citiesCopy = ArrayList<City>()
    var update: ((Int?) -> Unit) = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder =
        CityViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.city_list_item,
                parent,
                false
            )
        )

    fun swapData(citiesList: ArrayList<City>) {
        citiesCopy.clear()
        citiesCopy.addAll(citiesList)
        this.cities = citiesList
        notifyDataSetChanged()
    }

    fun setCheckedItemId(id: Int) {
        checkedItemId = id
    }

    fun getCheckedItem() = cities.find { city -> city.id == checkedItemId }

    override fun getItemCount() = cities.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) =
        holder.bind(cities[position])

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: City) = with(itemView) {

            name.text = data.name
            checkForCheckedItem(data)
            setOnClickListener {
                checkForCheckedItem(data)
                checkedItemId = data.id
                update.invoke(checkedItemId)
                notifyDataSetChanged()
            }
        }

        private fun checkForCheckedItem(data: City) = with(itemView) {
            if (checkedItemId == data.id) {
                updateUi(Typeface.DEFAULT_BOLD, "#161C25")
                is_clicked_image.visible()
            } else {
                updateUi(Typeface.DEFAULT, "#7B818C")
                is_clicked_image.gone()
            }
        }

        private fun updateUi(typeface: Typeface, color: String) = with(itemView) {
            name.typeface = typeface
            name.setTextColor(Color.parseColor(color))
        }
    }

    fun filter(queryText: String?) {
        checkedItemId = null
        update.invoke(checkedItemId)
        cities.clear()
        when {
            queryText.isNullOrEmpty() -> cities.addAll(citiesCopy)
            else -> citiesCopy.forEach { item ->
                if (item.name!!.contains(queryText, ignoreCase = true)) cities.add(item)
            }
        }
        notifyDataSetChanged()
    }
}