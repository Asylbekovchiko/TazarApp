package com.io.tazarapp.ui.auth.login.city

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.signUp.SignUpViewModel
import com.io.tazarapp.ui.citizen.CitizenActivity
import com.io.tazarapp.ui.recycler.RecyclerActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_city.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CityActivity : BaseActivity() {
    override fun handleCustomError(message: String) {
        toast(message)
    }

    private val viewModelCity: CityViewModel by viewModel()
    private val viewModel: SignUpViewModel by viewModel()
    var phoneNumber: String = ""
    var userType: String = ""

    private var isFilter = false
    private var selectedCity: CityModel? = null

    private lateinit var adapterCity: CityAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        isFilter = intent.getBooleanExtra(PARENT, false)
        initRV()
        initViewModel()
        search()
        listeners()
        configureViewModel()
        if (!isFilter) {
            phoneNumber = intent.getStringExtra(PHONE_NUMBER)
            userType = intent.getStringExtra(USER_TYPE)
        } else {
            clickItem(intent.getSerializableExtra(CITY) as CityModel?)
            adapterCity.selectedCity = selectedCity
            adapterCity.notifyDataSetChanged()
        }
    }

    private fun listeners() {
        btn_city_next.setOnClickListener {
            if (isFilter) {
                val filterIntent = Intent()
                filterIntent.putExtra(CITY, selectedCity)
                setResult(Activity.RESULT_OK, filterIntent)
                finish()
            } else {
                val mUser = FirebaseAuth.getInstance().currentUser
                mUser!!.getIdToken(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val idToken: String = task.result?.token.toString()
                                viewModel.postAuthData(
                                        clearPhoneNumb(phoneNumber),
                                        userType,
                                        selectedCity!!.id,
                                        idToken
                                )
                            }
                        }
            }
        }

        back_city.setOnClickListener {
            if (isFilter) {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    private fun search() {
        adapterCity.searchViewCollapsed()
        val searchView: SearchView = sv_city
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterCity.filter(newText)
                return true
            }
        })
    }

    private fun initRV() {
        adapterCity =
                CityAdapter { item: CityModel? ->
                    clickItem(item)
                }
        rv_city.layoutManager = LinearLayoutManager(this)
        rv_city.adapter = adapterCity
    }


    private fun clickItem(item: CityModel?) {
        selectedCity = item
        if (selectedCity != null) {
            btn_city_next.activate()
        } else {
            btn_city_next.disActivate()
        }
    }

    private fun initViewModel() {
        with(viewModelCity) {
            state.observe(this@CityActivity, Observer {
                when (it) {
                    is State.LoadingState -> {
                        when {
                            it.isLoading -> showDialog()
                            else -> hideDialog()
                        }
                    }
                    is State.ErrorState -> {
                        handleError(it)
                    }
                    is State.SuccessListState<*> -> {
                        when {
                            it.data[0] is CityModel -> {
                                adapterCity.update(it.data as ArrayList<CityModel>)
                            }
                        }
                    }
                }
            })
            getCity()
        }
    }

    private fun configureViewModel() {
        viewModel.state.observe(this, Observer { state ->
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
                        is AuthResponseModel -> {
                            SharedPrefModule(this).TokenModule().saveToken(state.data)
                            viewModel.saveDeviceId()
                            when (state.data.user_type) {
                                CITIZEN -> {
                                    startActivity<CitizenActivity>()
                                    finish()
                                }
                                PROCESSOR -> {
                                    startActivity<RecyclerActivity>()
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}