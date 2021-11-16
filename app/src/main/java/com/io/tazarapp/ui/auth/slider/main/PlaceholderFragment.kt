package com.io.tazarapp.ui.auth.slider.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.ui.auth.login.LoginActivity
import com.io.tazarapp.utils.LanguagePref

class Placeholder : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.start_slider, container, false)
        val im: ImageView = root.findViewById(R.id.image)
        val heading_text_np: TextView = root.findViewById(R.id.heading_text_np)
        val heading_text_np_2: TextView = root.findViewById(R.id.heading_text_np_2)
        val btn: Button = root.findViewById(R.id.button)
        val page = requireArguments().getInt("page")
        btn.setOnClickListener {
            LanguagePref.FirstTimeModule().saveFirstTime(requireContext(),false)
            startActivity(Intent(context, LoginActivity::class.java))
        }
        when (page) {
            1 -> {
                Glide.with(this).load(R.drawable.ic_slider_intro_icon_1).into(im)
                heading_text_np.text = getString(R.string.slide_title_description_1)
                heading_text_np_2.text = getString(R.string.slide_description_1)
            }
            2 -> {
                Glide.with(this).load(R.drawable.ic_slider_intro_icon_2).into(im)
                heading_text_np.text = getString(R.string.slide_title_description_2)
                heading_text_np_2.text = getString(R.string.slide_description_2)
            }
            3 -> {
                Glide.with(this).load(R.drawable.ic_slider_intro_icon_3).into(im)
                heading_text_np.text = getString(R.string.slide_title_description_3)
                heading_text_np_2.text = getString(R.string.slide_description_3)
            }
        }
        return root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "page"
        fun newInstance(sectionNumber: Int): Placeholder {
            return Placeholder().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}