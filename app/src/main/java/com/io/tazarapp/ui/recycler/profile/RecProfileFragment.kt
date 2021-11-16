package com.io.tazarapp.ui.recycler.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.BuildConfig
import com.io.tazarapp.R
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.LoginActivity
import com.io.tazarapp.ui.auth.start.LanguageActivity
import com.io.tazarapp.ui.citizen.filter.view.FilterCategoryActivity
import com.io.tazarapp.ui.citizen.profile.item_profile.about_info.AboutAppActivity
import com.io.tazarapp.ui.recycler.profile.item_profile.profile_info.ResProfileActivity
import com.io.tazarapp.ui.recycler.profile.item_profile.types.TypeActivity
import com.io.tazarapp.ui.recycler.schedule.ScheduleActivity
import com.io.tazarapp.ui.recycler.schedule.ScheduleViewModel
import com.io.tazarapp.ui.shared_ui.new_cp.NewCollectionPointActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_rec_profile.*
import kotlinx.android.synthetic.main.fragment_rec_profile.const_about
import kotlinx.android.synthetic.main.fragment_rec_profile.const_new
import kotlinx.android.synthetic.main.fragment_rec_profile.const_profile
import kotlinx.android.synthetic.main.fragment_rec_profile.const_report
import kotlinx.android.synthetic.main.fragment_rec_profile.image_profile
import kotlinx.android.synthetic.main.fragment_rec_profile.ln_profile_det
import kotlinx.android.synthetic.main.fragment_rec_profile.name_profile_info
import kotlinx.android.synthetic.main.fragment_rec_profile.prog_text
import kotlinx.android.synthetic.main.fragment_rec_profile.tv_exit_account
import kotlinx.android.synthetic.main.fragment_rec_profile.tv_next_weight_profile
import kotlinx.android.synthetic.main.fragment_rec_profile.tv_raw_profile
import kotlinx.android.synthetic.main.maps_page.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class RecProfileFragment : BottomSelectedFragment() {
    private val viewModel: ScheduleViewModel by viewModel()
    private var idUser: Int = 0
    override var selectedItem = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rec_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        version.text = "v ${BuildConfig.VERSION_NAME}"

        listeners()
        initToolbar()
        initViewModel()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.custom_title.text = resources.getString(R.string.profile_title)
    }

    private fun initViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state == null) {
                Log.e("Empty", "Item class is empty")
            } else {
                when (state) {
                    is State.LoadingState -> {
                        when {
                            state.isLoading -> showDialog()
                            else -> hideDialog()
                        }
                    }
                    is State.ErrorState -> {
                        handleError(state)
                    }
                    is State.SuccessObjectState<*> -> {
                        when (state.data) {
                            is ProfileRecyclerModel -> {
                                with(state.data) {
                                    tv_raw_profile.text = "$experience" + getString(R.string.kg)
                                    Glide.with(requireContext()).load(image)
                                        .circleCrop().into(image_profile)
                                    name_profile_info.text(title)
                                    idUser = id

                                    ln_profile_det.progressShow(
                                        prog_text,
                                        experience,
                                        upper_line,
                                        bottom_line
                                    )

                                    prog_text.text = experience.toString() + getString(R.string.kg)
                                    tv_next_weight_profile.text = "+ $experience "
                                }
                            }

                            204 -> {
                                SharedPrefModule(requireContext()).TokenModule().deleteToken()
                                val i = Intent(this.context, LoginActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(i)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun listeners() {
        const_profile.setOnClickListener { startActivity<ResProfileActivity>() }
        const_about.setOnClickListener { startActivity<AboutAppActivity>() }
        const_report.setOnClickListener { startActivity<ScheduleActivity>() }
        const_types.setOnClickListener {
            val intent = Intent(requireContext(), TypeActivity::class.java)
            intent.putExtra("idUser", idUser)
            startActivity(intent)
        }
        const_new.setOnClickListener {
            val newIntent = Intent(requireContext(), FilterCategoryActivity::class.java)
            newIntent.putExtra(POINT, true)
            newIntent.putExtra(IS_FILTER_CREATING, true)
            startActivity(newIntent)
        }
        const_language.setOnClickListener { startActivity<LanguageActivity>() }
        tv_exit_account.setOnClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            with(builder) {
                setTitle(getString(R.string.go_out))
                setMessage(getString(R.string.are_you_sure))
                setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.deleteRegistrationId()
                }
                setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.dismiss()
                }
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMainProfile()
    }
}