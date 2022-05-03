package com.example.android.validationproject.Validation

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

abstract class EditTextValidator(private var editText: TextInputEditText?) : TextWatcher{

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        val str: String = editText?.text.toString()
        editText?.let { validate(it, str) }
    }

    abstract fun validate(editText: TextInputEditText, text: String)

}