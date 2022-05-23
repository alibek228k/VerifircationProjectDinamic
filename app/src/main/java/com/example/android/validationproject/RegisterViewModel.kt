package com.example.android.validationproject

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.validationproject.Validation.Errors
import com.example.android.validationproject.Validation.Validators
import com.example.android.validationproject.data.SpecialProperties
import com.example.android.validationproject.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val fieldPromptsMutableLiveData = MutableLiveData<MutableList<FieldPrompt>>()
    private val resultMutableLiveData = MutableLiveData<Result>()
    private val submitMutableLiveData = MutableLiveData<Submit>()
    private val descAndTitleMutableLiveData = MutableLiveData<DescriptionAndTitle>()

    val resultLiveData: LiveData<Result>
        get() = resultMutableLiveData

    val submitLiveData: LiveData<Submit>
        get() = submitMutableLiveData

    val fieldPromptsLiveData: LiveData<MutableList<FieldPrompt>>
        get() = fieldPromptsMutableLiveData

    val descriptionAndTitleLiveData: LiveData<DescriptionAndTitle>
        get() = descAndTitleMutableLiveData

    private var fields: ArrayList<Field> = ArrayList()
    private val fieldPrompts = mutableListOf<FieldPrompt>()

    init {
        fields = setupJSONFieldsParsing()
        for (i in 0 until fields.size) {
            fieldPrompts.add(FieldPrompt(fields[i], value = null))
        }
        fieldPromptsMutableLiveData.postValue(fieldPrompts)
        descAndTitleMutableLiveData.postValue(DescriptionAndTitle(getDescription()?: "null", getTitle()?: "Title"))

    }

    fun onTextChanged(field: Field, position: Int, value: String?) {
        val fieldPrompt = fieldPrompts.find { it.field.id == field.id }
        if (fieldPrompt == null) {
            fieldPrompts.add(FieldPrompt(field, value))
        } else {
            fieldPrompt.value = value
        }
        if (fieldPrompt?.field?.isRequired == true) {
            if (value.isNullOrBlank()) {
                fieldPrompt.errors = fieldPrompt.field.errors?.blank!!
                resultMutableLiveData.postValue(Result(position, fieldPrompt.field.errors?.blank))
            } else if (fieldPrompt.field.validators != null) {
                val regex: String? = fieldPrompt.field.validators?.regex
                if (!regex.isNullOrBlank()) {
                    if (value.matches(regex.toRegex())) {
                        resultMutableLiveData.postValue(Result(position, "success"))
                        fieldPrompt.errors = "success"
                        Log.d("ALIBEK", "No error: ${fieldPrompt.errors}")
                    } else {
                        resultMutableLiveData.postValue(
                            Result(
                                position,
                                fieldPrompt.field.errors?.regex
                            )
                        )
                        fieldPrompt.errors = fieldPrompt.field.errors?.regex!!
                        Log.d("ALIBEK", "There is an error: ${fieldPrompt.errors}")
                        Log.d("ALIBEK", "$regex")
                    }
                }
            } else if (fieldPrompt.field.targetFieldId != null) {
                val value2 = fieldPrompts.find { it.field.id == fieldPrompt.field.targetFieldId }?.value
                if (value == value2) {
                    resultMutableLiveData.postValue(Result(position, "success"))
                    fieldPrompt.errors = "success"
                } else {
                    resultMutableLiveData.postValue(
                        Result(
                            position,
                            fieldPrompt.field.errors?.targetValueMismatch
                        )
                    )
                    fieldPrompt.errors = fieldPrompt.field.errors?.targetValueMismatch!!
                }
            } else {
                resultMutableLiveData.postValue(Result(position, "success"))
                fieldPrompt.errors = "success"
            }
        } else {
            resultMutableLiveData.postValue(Result(position, "success"))
            fieldPrompt?.errors = "success"
        }


    }

    private fun setupJSONFieldsParsing(): ArrayList<Field> {
        val fields = ArrayList<Field>()
        try {
            val jsonArray = JSONArray(jsonDataFromAssets("fields.json"))
            for (i in 0 until jsonArray.length()) {
                val fieldData = jsonArray.getJSONObject(i)
                val field = Field()
                val specs = SpecialProperties()
                val validators = Validators()
                val errors = Errors()
                field.id = fieldData.getInt("id")
                field.title = fieldData.getString("title")
                field.description = fieldData.getString("description")
                field.isRequired = fieldData.getBoolean("is_required")
                if (fieldData.has("target_field_id")) {
                    field.targetFieldId = fieldData.getInt("target_field_id")
                }

                if (fieldData.has("specs")) {
                    if (fieldData.getJSONObject("specs")
                            .has("input_text_max_length") && fieldData.getJSONObject("specs")
                            .has("input_text_max_lines")
                    ) {
                        specs.inputMaxLength =
                            fieldData.getJSONObject("specs").getInt("input_text_max_length")
                        specs.inputMaxLines =
                            fieldData.getJSONObject("specs").getInt("input_text_max_lines")
                        field.specs = specs
                    } else if (fieldData.getJSONObject("specs").has("input_text_max_length")) {
                        specs.inputMaxLength =
                            fieldData.getJSONObject("specs").getInt("input_text_max_length")
                        field.specs = specs
                    } else if (fieldData.getJSONObject("specs").has("input_text_max_lines")) {
                        specs.inputMaxLines =
                            fieldData.getJSONObject("specs").getInt("input_text_max_lines")
                        field.specs = specs
                    }
                }

                if (fieldData.has("validators")) {
                    if (fieldData.getJSONObject("validators")
                            .has("regex")
                    ) {
                        validators.regex =
                            fieldData.getJSONObject("validators").getString("regex")
                        field.validators = validators
                    }
                }


                if (fieldData.has("errors")) {
                    if (fieldData.getJSONObject("errors")
                            .has("blank") && fieldData.getJSONObject("errors")
                            .has("regex")
                    ) {
                        errors.blank =
                            fieldData.getJSONObject("errors").getString("blank")
                        errors.regex =
                            fieldData.getJSONObject("errors").getString("regex")
                        field.errors = errors
                    } else if (fieldData.getJSONObject("errors")
                            .has("blank") && fieldData.getJSONObject("errors")
                            .has("target_value_mismatch")
                    ) {
                        errors.blank =
                            fieldData.getJSONObject("errors").getString("blank")
                        errors.targetValueMismatch =
                            fieldData.getJSONObject("errors").getString("target_value_mismatch")
                        field.errors = errors
                    } else if (fieldData.getJSONObject("errors").has("blank")) {
                        errors.blank =
                            fieldData.getJSONObject("errors").getString("blank")
                        field.errors = errors
                    } else if (fieldData.getJSONObject("errors").has("regex")) {
                        errors.regex =
                            fieldData.getJSONObject("errors").getString("regex")
                        field.errors = errors
                    }
                }
                //type
                if (fieldData.getString("type") == "input_text") {
                    field.type = Field.Type.INPUT_TEXT
                } else if (fieldData.getString("type") == "date_selection") {
                    field.type = Field.Type.DATE_SELECTION
                } else if (fieldData.getString("type") == "password") {
                    field.type = Field.Type.PASSWORD
                } else if (fieldData.getString("type") == "password_confirmation") {
                    field.type = Field.Type.PASSWORD_CONFIRMATION
                }

                fields.add(field)



            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return fields
    }


    private fun getTitle(): String? {
        val title: String
        try {
            val jsonObject = JSONObject(jsonDataFromAssets("form.json"))
            title = jsonObject.getString("title")
            return title
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getDescription(): String? {
        val description: String
        try {
            val jsonObject = JSONObject(jsonDataFromAssets("form.json"))
            description = jsonObject.getString("description")
            return description
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    fun onSubmit() {
        for (i in 0 until fieldPrompts.size) {
            val fieldPrompt = fieldPrompts[i]
            if (fieldPrompt.field.isRequired == true) {
                if (fieldPrompt.value.isNullOrBlank()) {
                    val prompt = fieldPrompt.field.title

                    submitMutableLiveData.postValue(
                        Submit(
                            "There is an empty prompt: $prompt",
                            false
                        )
                    )
                    return
                }
            }
        }
        if (isThereAnyError()) {
            submitMutableLiveData.postValue(Submit("There is an error with ${errorName()} prompt", false))
        } else {
            submitMutableLiveData.postValue(Submit(getConfMessage(), true))
        }
    }

    private fun getConfMessage(): String {
        try {
            val jsonObject = JSONObject(jsonDataFromAssets("form.json"))
            return jsonObject.getJSONObject("presentation").getJSONObject("submission")
                .getString("confirmation_message")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isThereAnyError(): Boolean{
        for (i in 0 until fieldPrompts.size){
            if (fieldPrompts[i].errors != "success"){
                Log.d("ALIBEK", "Error: ${fieldPrompts[i].errors}")
                return true
            }
        }
        return false
    }

    private fun errorName(): String? {
        for (i in 0 until fieldPrompts.size){
            if (fieldPrompts[i].errors != "success"){
                return fieldPrompts[i].field.title
            }
        }
        return ""
    }

    private fun jsonDataFromAssets(filename: String): String {
        val json: String
        try {
            val inputStream: InputStream = getApplication<Application>().assets.open(filename)
            val sizeOfFile = inputStream.available()
            val bufferedData = ByteArray(sizeOfFile)
            inputStream.read(bufferedData)
            inputStream.close()
            json = String(bufferedData, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        return json
    }


}