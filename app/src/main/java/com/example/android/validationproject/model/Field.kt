package com.example.android.validationproject.model

import com.example.android.validationproject.Validation.Errors
import com.example.android.validationproject.Validation.Validators
import com.example.android.validationproject.data.SpecialProperties

class Field constructor(
    var id: Int? = null,
    var title: String? = null,
    var type: Type? = null,
    var description: String? = null,
    var isRequired: Boolean? = null,
    var specs: SpecialProperties? = null,
    var validators: Validators? = null,
    var errors: Errors? = null,
    var targetFieldId: Int? = null
) {
    enum class Type {
        INPUT_TEXT,
        DATE_SELECTION,
        PASSWORD,
        PASSWORD_CONFIRMATION
    }
}