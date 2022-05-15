package com.example.android.validationproject.Validation

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

fun interface EditTextValidator : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
//        val str = editable?.toString()
//        if (!str.isNullOrBlank()) {
            validate(editable?.toString())
//        }
    }

    fun validate(text: String?)

}