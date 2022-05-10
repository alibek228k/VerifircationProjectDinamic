package com.example.android.validationproject.modal

import com.example.android.validationproject.Validation.Errors
import com.example.android.validationproject.Validation.Validators
import com.example.android.validationproject.data.SpecialProperties


class Fields(var id: Int?, var title: String?, var type: InputType?, var description: String?, var isRequired: Boolean?, var specs: SpecialProperties?, var validators: Validators?, var errors: Errors?, var targetFieldId: Int? = null) {
    constructor() : this(null, null, null, null, null, null, null, null, null)


    enum class InputType{
        INPUT_TEXT,
        DATE_SELECTION,
        PASSWORD,
        PASSWORD_CONFIRMATION
    }
}