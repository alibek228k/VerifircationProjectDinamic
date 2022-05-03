package com.example.android.validationproject.Validation

import java.util.regex.Pattern

abstract class StringValidation<T> constructor(

) : Validator<T>() {

    abstract var value: T?

    open fun hasValue(): Boolean = false

}