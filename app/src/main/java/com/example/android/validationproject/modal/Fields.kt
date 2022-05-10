package com.example.android.validationproject.modal

import com.example.android.validationproject.data.SpecialProperties


class Fields(var id: Int, var title: String, var type: InputType, var description: String, var isRequired: Boolean, var specs: SpecialProperties var regex: String ) {

    enum class InputType{
        INPUT_TEXT,
        DATE_SELECTION,
        PASSWORD,
        PASSWORD_CONFIRMATION
    }
}