package com.io.tazarapp.ui.citizen.ad.adCreate.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.SubCategory
import com.io.tazarapp.utils.afterTxtChanged
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.item_raw_materials.view.*


class RawMaterialsAdapter(val isNewPoint:Boolean=false) : RecyclerView.Adapter<RawMaterialsAdapter.RawMaterialsViewHolder>() {
    var items: ArrayList<SubCategory> = arrayListOf()
    var verifyItems: ((ArrayList<SubCategory>) -> Unit) = { }
    var deleteItem: ((Int) -> Unit) = { }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RawMaterialsViewHolder {
        return RawMaterialsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_raw_materials,
                parent,
                false
            )
        )
    }

    fun swapData(items: ArrayList<SubCategory>) {
        this.items = items
        notifyDataSetChanged()
        verifyItems.invoke(this.items)
    }

    fun getData() = items
    fun getIDs(): ArrayList<Int> {
        val ids = arrayListOf<Int>()
        items.forEach {
            it.subcategory?.let { it1 -> ids.add(it1) }
        }
        return ids
    }

    override fun onBindViewHolder(holder: RawMaterialsViewHolder, position: Int) =
        holder.bind(items[position])

    inner class RawMaterialsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle = view.txt_title!!
        private val editWeight = view.edit_weight!!
        private val delete = view.btn_delete!!

        fun bind(item: SubCategory) = with(itemView) {
            if (isNewPoint){
                editWeight.gone()
            }else{
                editWeight.visible()
            }
            txtTitle.text = item.name.toString()

            if (item.value != null) {
                editWeight.setText(item.value.toString())
            } else {
                editWeight.setText("")
            }

            editWeight.afterTxtChanged {
                items[adapterPosition].value = editWeight.text.toString().toIntOrNull()
                verifyItems.invoke(items)
            }

            delete.setOnClickListener {
                items[adapterPosition].subcategory?.let { it1 -> deleteItem.invoke(it1) }
                items.removeAt(adapterPosition)
                verifyItems.invoke(items)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}