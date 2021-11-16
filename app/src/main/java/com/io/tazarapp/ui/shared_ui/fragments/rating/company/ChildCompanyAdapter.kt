package com.io.tazarapp.ui.shared_ui.fragments.rating.company

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.rating.RateListModel
import kotlinx.android.synthetic.main.item_child_company.view.*

class ChildCompanyAdapter( val children: List<RateListModel>, val function: (List<RateListModel>) -> Unit) :
    RecyclerView.Adapter<ChildCompanyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child_company, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val child = children[position]
        holder.imageView.setImageResource(child.icon)
        holder.lvl.text = child.lvl
        holder.name.text = child.name
        holder.city.text = child.city
        holder.weight.text = child.weight

        holder.itemView.setOnClickListener {
            function(children)

        }
    }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val lvl: TextView = itemView.tv_lvl
        val name: TextView = itemView.tv_name
        val city: TextView = itemView.tv_city
        val weight: TextView = itemView.tv_weight_result
        val imageView: ImageView = itemView.imgIcon

    }

}