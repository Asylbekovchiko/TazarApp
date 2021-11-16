package com.io.tazarapp.ui.shared_ui.fragments.rating.company

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.rating.CompanyModel
import com.io.tazarapp.data.model.rating.RateListModel
import kotlinx.android.synthetic.main.item_company.view.*

class CompanyAdapter(private val function: (List<RateListModel>) -> Unit)  :
    RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {
    private var data: MutableList<CompanyModel> = mutableListOf()
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }


    fun update(list: ArrayList<CompanyModel>) {
        this.data = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(
        holder: CompanyViewHolder,
        position: Int
    ) {
        val parent = data[position]
        holder.titleCompany.text = parent.title
        holder.imgCompany.setImageResource(parent.icon)

        val childLayoutManager =
            LinearLayoutManager(holder.recyclerView.context)
//        childLayoutManager.initialPrefetchItemCount = 10
        holder.recyclerView.apply {
            layoutManager = childLayoutManager
            adapter = ChildCompanyAdapter(parent.rateList, function)
            setRecycledViewPool(viewPool)
        }

    }


    class CompanyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imgCompany: ImageView = itemView.img_company
        val titleCompany: TextView = itemView.title_company
        val recyclerView: RecyclerView = itemView.rv_inside_company
    }

}