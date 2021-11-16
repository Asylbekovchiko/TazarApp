package com.io.tazarapp.utils

import android.app.AlertDialog
import android.content.Intent
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.io.tazarapp.R
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.LoginActivity
import com.io.tazarapp.utils.gsm.FCMTokenUtils
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

abstract class BaseFragment() : Fragment() {
    var dialog: AlertDialog? = null

    fun showDialog() {
        if (dialog == null) {
            dialog =
                SpotsDialog.Builder().setContext(requireContext()).setMessage(R.string.loading_message)
                    .build()
            dialog!!.show()
        }
    }

    fun hideDialog() {
        if (dialog != null) {
            dialog!!.cancel()
            dialog = null
        }
    }


    fun handleError(state: State.ErrorState) {
        Log.e("Error",state.message)
        FirebaseCrashlytics.getInstance().also {

            it.setCustomKey(state.errorCode.toString(), state.message)
            it.log(state.message)
            it.recordException(Throwable(state.errorCode.toString()))
        }.sendUnsentReports()


        when (state.errorCode) {
            DEFAUL_INTEGER -> requireContext().toast(getString(R.string.error_network))
            DEFAULT_NO_INTERNER_INTEGER -> requireContext().toast(getString(R.string.error_no_internet))
            401 -> CoroutineScope(Dispatchers.IO).launch {
                SharedPrefModule(requireContext()).TokenModule().deleteToken()
                withContext(Dispatchers.Main) {
                    startActivity(
                        Intent(requireContext(), LoginActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    )
                }
            }
            500 -> requireContext().toast(getString(R.string.error_server))
            404 -> requireContext().toast(getString(R.string.error_not_found))
            413 -> requireContext().toast(getString(R.string.error_big_file))
            else -> {

                try {
                    val jsonObject = JSONObject(state.message)

                    when {
                        jsonObject.has("detail") -> try {
                            val message = jsonObject.getString("detail")
                            requireContext().toast(message)
                        } catch (e: Exception) {
                            val jsonArray = jsonObject.getJSONArray("detial")
                            if (jsonArray.length() > 0) {
                                requireContext().toast(jsonArray.getString(1))
                            }
                        }
                        jsonObject.has("message") -> try {
                            val message = jsonObject.getString("message")
                            requireContext().toast(message)
                        } catch (e: Exception) {
                            val jsonArray = jsonObject.getJSONArray("message")
                            if (jsonArray.length() > 0) {
                                requireContext().toast(jsonArray.getString(1))
                            }
                        }
                        else -> handleCustomError(state.message)
                    }
                } catch (e: Exception) {
                    handleCustomError(state.message)
                }
            }
        }

    }

    open fun handleCustomError(message: String){}
}
