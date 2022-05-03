package com.example.android.validationproject.Validation


abstract class Validator<T> {
    sealed class Result<R> constructor(
        open val value: R?
    ){
        data class Success<S> constructor(
            override val value: S?
        ): Result<S>(value)

        abstract class Error<E> constructor(
            override val value: E?,
            open val message: String? = null
        ): Result<E>(value)

    }

    private var result: Result<T>? = null

    fun isValid(): Boolean  = result is Result.Success

    fun getResult(): Result<T> ?= result

    fun setResult(result: Result<T>): Boolean{
        this.result = result
        return this.result == result
    }


    abstract fun validate(): Result<T>
}