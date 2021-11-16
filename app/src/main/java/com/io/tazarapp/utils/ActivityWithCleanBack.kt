package com.io.tazarapp.utils

import android.content.Intent

abstract class ActivityWithCleanBack : BaseActivity() {
    val success = 789

    fun startActivityWithCleanBack(i : Intent) {
        startActivityForResult(i, success)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            if (requestCode == success){
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}