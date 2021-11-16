package com.io.tazarapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class CodeVerificationContainer(private vararg val editTexts: EditText,
                                private val listener: (Boolean) -> Unit) {

    init {
        var editText = editTexts[0]
        editText.addTextChangedListener(CodeTextWatcher(0))
        editText.setOnKeyListener(CodeOnKeyListener(0))
        for (i in 1 until editTexts.size) {
            editText = editTexts[i]
            editText.addTextChangedListener(CodeTextWatcher(i))
            editText.setOnKeyListener(CodeOnKeyListener(i))
            editText.isFocusableInTouchMode = false
        }
    }


    fun getCode(): String {
        val sb = StringBuilder()
        editTexts.forEach {
            sb.append(it.text)
        }

        return sb.toString()
    }

    private fun next(char: Char, lastPosition: Int) {
        val lastEditText = editTexts[lastPosition]
        if (lastPosition == editTexts.size - 1) {
            listener(true)
            lastEditText.hideKeyboard()
            return
        }
        listener(false)
        lastEditText.isFocusableInTouchMode = false
        val nextEditText = editTexts[lastPosition + 1]
        nextEditText.isFocusableInTouchMode = true
        nextEditText.setText(char.toString())
        nextEditText.requestFocus()
    }

    private fun prev(lastPosition: Int) {
        val lastEditText = editTexts[lastPosition]
        if (lastPosition == 0) {
            return
        }
        lastEditText.isFocusableInTouchMode = false
        val prevEditText = editTexts[lastPosition - 1]
        prevEditText.isFocusableInTouchMode = true
        prevEditText.requestFocus()
    }

    private inner class CodeTextWatcher(private val position: Int) : TextWatcher {

        private var start = 0

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            this.start = start
            if ((position == editTexts.size - 1) && (s?.length == 1)) {
                next(' ', position)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (s == null) return
            if (s.length > 1) {
                val char: Char
                if (start == 0) {
                    char = s[0]
                    s.delete(0, 1)
                } else {
                    char = s[1]
                    s.delete(1, 2)
                }
                next(char, position)
            }
        }
    }

    private inner class CodeOnKeyListener(private val position: Int) : View.OnKeyListener {

        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (v !is EditText) return false
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (v.text.isEmpty()) {
                    prev(position)
                } else {
                    v.setText("")
                }
                return true
            }

            return false
        }

    }
}