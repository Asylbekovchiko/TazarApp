package com.io.tazarapp.ui.shared_ui.fragments.rating.company

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.rating.CompanyModel
import com.io.tazarapp.data.model.rating.RateListModel
import com.io.tazarapp.ui.shared_ui.fragments.rating.RatingDetActivity
import kotlinx.android.synthetic.main.fragment_company.*

class CompanyFragment : Fragment() {

    //    private val viewModel: HistoryViewModel by viewModel()
    private lateinit var adapterCompany: CompanyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        initRV()
    }

    private fun listeners() {

    }

    private fun initRV() {
        adapterCompany = CompanyAdapter { item: List<RateListModel> -> clickItem(item) }
        rv_outside_company.layoutManager = LinearLayoutManager(this.context)
        rv_outside_company.adapter = adapterCompany
        adapterCompany.update(updateData())

    }

    private fun clickItem(item: List<RateListModel>) {
        val intent = Intent(requireContext(), RatingDetActivity::class.java)
//        intent.putExtra("ids", item.id.toString())
        startActivity(intent)
    }

    private fun updateData(): ArrayList<CompanyModel> {
        val list = ArrayList<CompanyModel>()
        val list2 = ArrayList<RateListModel>()
        list2.add(
            RateListModel(
                0,
                "1 место",
                "Саша",
                R.drawable.ic_circle_superhero,
                "Бишкек",
                "500 кг"
            )
        )
        list2.add(
            RateListModel(
                1,
                "2 место",
                "Маша",
                R.drawable.ic_circle_general,
                "Ош",
                "480 кг"
            )
        )
        list2.add(
            RateListModel(
                2,
                "3 место",
                "Даша",
                R.drawable.ic_circle_general,
                "Каракол",
                "470 кг"
            )
        )
        list2.add(
            RateListModel(
                3,
                "4 место",
                "Каша",
                R.drawable.ic_circle_superhero,
                "Баткен",
                "460 кг"
            )
        )
        list2.add(
            RateListModel(
                4,
                "5 место",
                "Яша",
                R.drawable.ic_circle_general,
                "Кадамжай",
                "450 кг"
            )
        )
        val list3 = ArrayList<RateListModel>()
        list3.add(
            RateListModel(
                0,
                "1 место",
                "Абдулла",
                R.drawable.ic_circle_general,
                "Бишкек",
                "500 кг"
            )
        )
        list3.add(
            RateListModel(
                1,
                "2 место",
                "Магомед",
                R.drawable.ic_circle_superhero,
                "Ош",
                "480 кг"
            )
        )
        list3.add(
            RateListModel(
                2,
                "3 место",
                "Ибрагим",
                R.drawable.ic_circle_general,
                "Каракол",
                "470 кг"
            )
        )
        list3.add(
            RateListModel(
                3,
                "4 место",
                "Сулейман",
                R.drawable.ic_circle_general,
                "Баткен",
                "460 кг"
            )
        )
        list3.add(
            RateListModel(
                4,
                "5 место",
                "Мехмет",
                R.drawable.ic_circle_superhero,
                "Кадамжай",
                "450 кг"
            )
        )
        val list4 = ArrayList<RateListModel>()
        list4.add(
            RateListModel(
                0,
                "1 место",
                "Улан",
                R.drawable.ic_circle_general,
                "Бишкек",
                "500 кг"
            )
        )
        list4.add(
            RateListModel(
                1,
                "2 место",
                "Руслан",
                R.drawable.ic_circle_superhero,
                "Ош",
                "480 кг"
            )
        )
        list4.add(
            RateListModel(
                2,
                "3 место",
                "Самат",
                R.drawable.ic_circle_general,
                "Каракол",
                "470 кг"
            )
        )
        list4.add(
            RateListModel(
                3,
                "4 место",
                "Азамат",
                R.drawable.ic_circle_superhero,
                "Баткен",
                "460 кг"
            )
        )
        list4.add(
            RateListModel(
                4,
                "5 место",
                "Бектемир",
                R.drawable.ic_circle_general,
                "Кадамжай",
                "450 кг"
            )
        )

        list.add(
            CompanyModel(
                0,
                "Эко супергерои",
                R.drawable.ic_circle_superhero,
                list2
            )
        )
        list.add(
            CompanyModel(
                1,
                "Генералы Эко",
                R.drawable.ic_circle_general,
                list3
            )
        )
        list.add(
            CompanyModel(
                2,
                "Титаны Эко",
                R.drawable.ic_circle_general,
                list4
            )
        )
        return list
    }


}
