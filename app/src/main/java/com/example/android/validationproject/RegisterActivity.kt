package com.example.android.validationproject

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.Validation.Errors
import com.example.android.validationproject.Validation.Validators
import com.example.android.validationproject.adapter.FormAdapter
import com.example.android.validationproject.data.SpecialProperties
import com.example.android.validationproject.model.Field
import com.example.android.validationproject.model.FieldPrompt
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegisterActivity : AppCompatActivity(), FormAdapter.Listener {

    private var recyclerView: RecyclerView? = null
    private var title: TextView? = null
    private var desctiprion: TextView? = null
    private var button: MaterialButton? = null

    private var fields: ArrayList<Field> = ArrayList()

    private var confMessage: String? = null

    private var formInputs = arrayListOf<TextInputLayout>()
    private var formAdapter: FormAdapter? = null

    private val fieldPrompts = mutableListOf<FieldPrompt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        recyclerView = findViewById(R.id.recyclerView)
        button = findViewById(R.id.registerButton)
        title = findViewById(R.id.title)
        desctiprion = findViewById(R.id.description)
//        FormAdapter.setupRegisterButton(button)
        setupErrorEnabling()
        setupJSONFormParsing()
        setupJSONFieldsParsing()
        setupRecyclerView()
        setupRegisterButton()


    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView?.layoutManager = linearLayoutManager
        for (i in 0 until formAdapter?.itemCount!!){
            fieldPrompts.add(i, FieldPrompt(fields[i], value = null))
            val formInput = TextInputLayout(this)
            formInputs.add(formInput)
        }
    }



    private fun setupErrorEnabling() {

    }

    private fun setupJSONFormParsing() {
        try {
            val jsonObject = JSONObject(jsonDataFromAssets("form.json"))
            title?.text = jsonObject.getString("title")
            desctiprion?.text = jsonObject.getString("description")
            val presentation = jsonObject.getJSONObject("presentation")
            val submission = presentation.getJSONObject("submission")
            confMessage = submission.getString("confirmation_message")


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setupJSONFieldsParsing() {
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
                } else {
                    println("has nothing")
                }

                if (fieldData.has("validators")) {
                    if (fieldData.getJSONObject("validators")
                            .has("regex")
                    ) {
                        validators.regex =
                            fieldData.getJSONObject("validators").getString("regex")
                        field.validators = validators
                    }
                } else {
                    println("hasn't any type of validators")
                }


                if (fieldData.has("errors")) {
                    if (fieldData.getJSONObject("errors")
                            .has("blank") && fieldData.getJSONObject("errors")
                            .has("regex")
                    ) {
                        errors.blanc =
                            fieldData.getJSONObject("errors").getString("blank")
                        errors.regex =
                            fieldData.getJSONObject("errors").getString("regex")
                        field.errors = errors
                    } else if (fieldData.getJSONObject("errors")
                            .has("blank") && fieldData.getJSONObject("errors")
                            .has("target_value_mismatch")
                    ) {
                        errors.blanc =
                            fieldData.getJSONObject("errors").getString("blank")
                        errors.targetValueMismatch =
                            fieldData.getJSONObject("errors").getString("target_value_mismatch")
                        field.errors = errors
                    } else if (fieldData.getJSONObject("errors").has("blank")) {
                        errors.blanc =
                            fieldData.getJSONObject("errors").getString("blank")
                        field.errors = errors
                    } else if (fieldData.getJSONObject("errors").has("regex")) {
                        errors.regex =
                            fieldData.getJSONObject("errors").getString("regex")
                        field.errors = errors
                    }
                } else {
                    println("has nothing")
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

        formAdapter = FormAdapter(fields, this)
        recyclerView?.adapter = formAdapter
    }

    private fun jsonDataFromAssets(filename: String): String {
        val json: String
        try {
            val inputStream: InputStream = assets.open(filename)
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


    private fun setupDatePicker(dateEditText: TextInputLayout, context: Context) {

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayofMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayofMonth)
            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            dateEditText.editText?.setText(sdf.format(myCalendar.time))
        }

        dateEditText.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                context, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()

        }
    }


    override fun onTextChanged(field: Field, position: Int, value: String?) {
        val fieldPrompt = fieldPrompts.find { it.field.id == field.id }
        if (fieldPrompt != null) {
            fieldPrompt.value = value
        }


        if (fieldPrompt?.field?.isRequired == true) {
            if (value.isNullOrBlank()) {
                formAdapter?.onValidate(position, fieldPrompt.field.errors?.blanc!!, false)
                formInputs[position].error = fieldPrompt.field.errors?.blanc
            } else if (fieldPrompt.field.validators != null) {
                val regex: String? = fieldPrompt.field.validators?.regex
                if (!regex.isNullOrBlank()) {
                    println("Everything is okay: $regex")
                    if (value.matches(regex.toRegex())) {
                        println("It matches")
                        formAdapter?.onValidate(position, "asdf", true)
                        formInputs[position].error = null
                    }else{
                        println("It doesn't matches")
                        formAdapter?.onValidate(position, fieldPrompt.field.errors?.regex!!, false)
                        formInputs[position].error = fieldPrompt.field.errors?.regex
                    }
                }
            }else if (fieldPrompt.field.targetFieldId != null){
                val value2 = fieldPrompts[fieldPrompt.field.targetFieldId!! - 1].value
                if (value == value2){
                    formAdapter?.onValidate(position, "asdf", true)
                    formInputs[position].error = null
                }else{
                    formAdapter?.onValidate(position, fieldPrompt.field.errors?.targetValueMismatch!!, false)
                    formInputs[position].error = fieldPrompt.field.errors?.targetValueMismatch
                }
            } else {
                formAdapter?.onValidate(position, "asdf", true)
                formInputs[position].error = null
            }
        } else {
            formAdapter?.onValidate(position, "sdfg", true)
            formInputs[position].error = null
        }
    }

    override fun onDateInputClicked(formInput: TextInputLayout) {
        setupDatePicker(formInput, this)
    }


    private fun setupRegisterButton(){
        button?.setOnClickListener{
            for (i in 0 until fieldPrompts.size){
                val fieldPrompt = fieldPrompts[i]
                if (fieldPrompt.value.isNullOrBlank()){
                    val prompt = fieldPrompt.field.title
                    val toast = Toast.makeText(this, "There is an empty prompt: $prompt", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            if(isThereAnyError()){
                val toast = Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT)
                toast.show()
            }else{
                val intent = Intent(this, AnotherActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                val toast = Toast.makeText(this, confMessage, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun isThereAnyError(): Boolean{
        for (i in 0 until formInputs.size){
            if (formInputs[i].error != null){
                println("Error: " + formInputs[i].error)
                return true
            }
        }
        return false
    }
//    private fun setupRegisterButton() {
//        button?.setOnClickListener {
//            val firstname = firstNameEditText?.editText?.text.toString()
//            val lastname = firstNameEditText?.editText?.text.toString()
//            val username = firstNameEditText?.editText?.text.toString()
//            val iin = firstNameEditText?.editText?.text.toString()
//            val phoneNumber = firstNameEditText?.editText?.text.toString()
//            val birthday = firstNameEditText?.editText?.text.toString()
//            val password = firstNameEditText?.editText?.text.toString()
//            val passwordConfirm = firstNameEditText?.editText?.text.toString()
//            if ((firstNameEditText?.editText?.error == null &&
//                        lastNameEditText?.editText?.error == null &&
//                        usernameEditText?.editText?.error == null &&
//                        iinEditText?.editText?.error == null &&
//                        phoneNumberEditText?.editText?.error == null &&
//                        passwordEditText?.editText?.error == null &&
//                        passwordConfirmEditText?.editText?.error == null
//                        ) && !(firstname.isBlank() && lastname.isBlank() &&
//                        username.isBlank() && iin.isBlank() && phoneNumber.isBlank() &&
//                        birthday.isBlank() && password.isBlank() && passwordConfirm.isBlank()
//                        )
//            ) {
//                val intent = Intent(this@RegisterActivity, AnotherActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//                val toast = Toast.makeText(this, confMessage, Toast.LENGTH_SHORT)
//                toast.show()
//            }
//        }
//    }


}