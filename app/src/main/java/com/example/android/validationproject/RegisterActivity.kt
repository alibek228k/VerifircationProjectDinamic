package com.example.android.validationproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.Validation.EditTextValidator
import com.example.android.validationproject.Validation.Errors
import com.example.android.validationproject.Validation.Validators
import com.example.android.validationproject.adapter.FormAdapter
import com.example.android.validationproject.data.SpecialProperties
import com.example.android.validationproject.modal.Fields
import com.example.android.validationproject.modal.Form
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RegisterActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var title: TextView? = null
    private var desctiprion: TextView? = null
    private var firstNameEditText: TextInputLayout? = null
    private var lastNameEditText: TextInputLayout? = null
    private var middleNameEditText: TextInputLayout? = null
    private var usernameEditText: TextInputLayout? = null
    private var iinEditText: TextInputLayout? = null
    private var birthdayEditText: TextInputLayout? = null
    private var phoneNumberEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var passwordConfirmEditText: TextInputLayout? = null
    private var button: MaterialButton? = null

    private var fields: ArrayList<Fields> = ArrayList()



    private var confMessage: String? = null
    private var prefix = "+7"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        firstNameEditText = findViewById(R.id.firstNameEditText)
//        lastNameEditText = findViewById(R.id.lastNameEditText)
//        middleNameEditText = findViewById(R.id.middleNameEditText)
//        usernameEditText = findViewById(R.id.usernameEditText)
//        iinEditText = findViewById(R.id.iinEditText)
//        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
//        birthdayEditText = findViewById(R.id.birthdayEditText)
//        passwordEditText = findViewById(R.id.passwordEditText)
//        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText)
        recyclerView = findViewById(R.id.recyclerView)
        button = findViewById(R.id.registerButton)
        title = findViewById(R.id.title)
        desctiprion = findViewById(R.id.description)
        setupRegisterButton()
        setupBirthdayInputText()
        setupPhoneNumberFormat()
        setupFirstNameValidator()
        setupLastNameValidator()
        setupMiddleNameValidator()
        setupUsernameValidator()
        setupIINValidator()
        setupPhoneNumberValidator()
        setupBirthdayValidator()
        setupPasswordValidator()
        setupPasswordConfirmationValidator()
        setupErrorEnabling()
        setupJSONFormParsing()
        setupJSONFieldsParsing()
        setupRecyclerView()


    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView?.layoutManager = linearLayoutManager
    }

    private fun setupFirstNameValidator() {
        firstNameEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            firstNameEditText?.editText as TextInputEditText?
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val firstName: String = firstNameEditText?.editText?.text.toString().trim()
                if (firstName.isNullOrBlank()) {
                    firstNameEditText?.error = getString(R.string.first_name_error)
                } else {
                    firstNameEditText?.error = null
                }
            }
        })
    }

    private fun setupLastNameValidator() {
        lastNameEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            lastNameEditText?.editText as TextInputEditText?
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val lastName: String = lastNameEditText?.editText?.text.toString().trim()
                if (lastName.isNullOrBlank()) {
                    lastNameEditText?.error = getString(R.string.last_name_error)
                } else {
                    lastNameEditText?.error = null
                }
            }
        })
    }

    private fun setupMiddleNameValidator() {
        middleNameEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            middleNameEditText?.editText as TextInputEditText
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val middleName: String = middleNameEditText?.editText?.text.toString().trim()
                if (middleName.isNullOrBlank()) {
                    middleNameEditText?.error = getString(R.string.middle_name_error)
                } else {
                    middleNameEditText?.error = null
                }
            }
        })
    }

    private fun setupUsernameValidator() {
        usernameEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            usernameEditText?.editText as TextInputEditText?
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val regex: String = "^[a-z0-9_-]{3,15}$"
                val username: String = usernameEditText?.editText?.text.toString().trim()
                if (username.isNullOrBlank()) {
                    usernameEditText?.error = getString(R.string.username_error)
                } else if (!username.matches(regex.toRegex())) {
                    usernameEditText?.error = getString(R.string.username_format_error)
                } else {
                    usernameEditText?.error = null
                }
            }

        })
    }

    private fun setupIINValidator() {
        iinEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            iinEditText?.editText as TextInputEditText
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val iin: String = iinEditText?.editText?.text.toString().trim()
                when {
                    iin.isNullOrBlank() -> {
                        iinEditText?.error = getString(R.string.iin_error)
                    }
                    iin.length == 12 -> {
                        iinEditText?.error = null
                    }
                    else -> {
                        iinEditText?.error = getString(R.string.iin_format_error)
                    }
                }
            }
        })
    }

    private fun setupPhoneNumberValidator() {
        phoneNumberEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            birthdayEditText?.editText as TextInputEditText
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val regex: String = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{10,13}\$"
                val phoneNumber: String = phoneNumberEditText?.editText?.text.toString().trim()
                if (phoneNumber.isNullOrBlank()) {
                    phoneNumberEditText?.error = getString(R.string.phone_number_error)
                } else if (!phoneNumber.matches(regex.toRegex())) {
                    phoneNumberEditText?.error = getString(R.string.invalid_format)
                } else {
                    phoneNumberEditText?.error = null
                }
            }
        })
    }

    private fun setupBirthdayValidator() {
        birthdayEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            birthdayEditText?.editText as TextInputEditText
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val regex: String = "^\\d{2}-\\d{2}-\\d{4}\$"
                val birthday: String = birthdayEditText?.editText?.text.toString().trim()
                if (birthday.isNullOrBlank()) {
                    birthdayEditText?.error = getString(R.string.birthday_error)
                } else if (!birthday.matches(regex.toRegex())) {
                    birthdayEditText?.error = getString(R.string.invalid_format)
                } else {
                    birthdayEditText?.error = null
                }
            }
        })
    }

    private fun setupPasswordValidator() {
        passwordEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            passwordEditText?.editText as TextInputEditText
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val regex: String = "^(?=.{8,}\$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).*\$"
                val password: String = passwordEditText?.editText?.text.toString()
                if (password.isNullOrBlank()) {
                    passwordEditText?.error = getString(R.string.password_error)
                    passwordEditText?.errorIconDrawable = null
                } else if (!password.matches(regex.toRegex())) {
                    passwordEditText?.error = getString(R.string.password_format_error)
                    passwordEditText?.errorIconDrawable = null
                } else if (password != passwordConfirmEditText?.editText?.text?.toString()) {
                    passwordConfirmEditText?.error =
                        getString(R.string.passwords_should_be_same)
                    passwordEditText?.error = null
                    passwordEditText?.errorIconDrawable = null
                } else {
                    passwordEditText?.error = null
                }
            }
        })
    }

    private fun setupPasswordConfirmationValidator() {
        passwordConfirmEditText?.editText?.addTextChangedListener(object : EditTextValidator(
            passwordConfirmEditText?.editText as TextInputEditText
        ) {
            override fun validate(editText: TextInputEditText, text: String) {
                val password: String = passwordEditText?.editText?.text.toString()
                val confirmPassword: String = passwordConfirmEditText?.editText?.text.toString()
                when {
                    confirmPassword.isNullOrBlank() -> {
                        passwordConfirmEditText?.error = getString(R.string.password_confi_error)
                        passwordConfirmEditText?.errorIconDrawable = null
                    }
                    password != confirmPassword -> {
                        passwordConfirmEditText?.error =
                            getString(R.string.passwords_should_be_same)
                        passwordConfirmEditText?.errorIconDrawable = null
                    }
                    else -> {
                        passwordConfirmEditText?.error = null
                    }
                }
            }
        })
    }

    private fun setupRegisterButton() {
        button?.setOnClickListener {
            val firstname = firstNameEditText?.editText?.text.toString()
            val lastname = firstNameEditText?.editText?.text.toString()
            val username = firstNameEditText?.editText?.text.toString()
            val iin = firstNameEditText?.editText?.text.toString()
            val phoneNumber = firstNameEditText?.editText?.text.toString()
            val birthday = firstNameEditText?.editText?.text.toString()
            val password = firstNameEditText?.editText?.text.toString()
            val passwordConfirm = firstNameEditText?.editText?.text.toString()
            if ((firstNameEditText?.editText?.error == null &&
                        lastNameEditText?.editText?.error == null &&
                        usernameEditText?.editText?.error == null &&
                        iinEditText?.editText?.error == null &&
                        phoneNumberEditText?.editText?.error == null &&
                        passwordEditText?.editText?.error == null &&
                        passwordConfirmEditText?.editText?.error == null
                        ) && !(firstname.isBlank() && lastname.isBlank() &&
                        username.isBlank() && iin.isBlank() && phoneNumber.isBlank() &&
                        birthday.isBlank() && password.isBlank() && passwordConfirm.isBlank()
                        )
            ) {
                val intent = Intent(this@RegisterActivity, AnotherActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                val toast = Toast.makeText(this, confMessage, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun setupPhoneNumberFormat() {
        phoneNumberEditText?.editText?.setText(prefix)

        phoneNumberEditText?.editText?.addTextChangedListener(object :
            PhoneNumberFormattingTextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            private var backspacingFlag = false

            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private var editedFlag = false

            //we need to mark the cursor position and restore it after the edition
            private var cursorComplement = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length - phoneNumberEditText?.editText!!.selectionStart
                //we check if the user ir inputing or erasing a character
                backspacingFlag = count > after
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO do something
            }

            override fun afterTextChanged(s: Editable) {
                val string = s.toString()
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                val phone = string.replace("[^\\d]".toRegex(), "")

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if (!editedFlag) {

                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: (999) 999-999
                    if (phone.length >= 9 && !backspacingFlag) {
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true
                        //here is the core. we substring the raw digits and add the mask as convenient
                        val ans = "+7(" + phone.substring(1, 4) + ") " + phone.substring(
                            4, 7
                        ) + "-" + phone.substring(7, 9) + "-" + phone.substring(9)
                        phoneNumberEditText?.editText?.setText(ans)
                        //we deliver the cursor to its original position relative to the end of the string
                        phoneNumberEditText?.editText?.setSelection(phoneNumberEditText?.editText?.text!!.length - cursorComplement)

                        //we end at the most simple case, when just one character mask is needed
                        //example: 99999 <- 3+ digits already typed
                        // masked: (999) 99
                    } else if (phone.length >= 4 && !backspacingFlag) {
                        editedFlag = true
                        val ans = "+7(" + phone.substring(1, 4) + ") " + phone.substring(4)
                        phoneNumberEditText?.editText?.setText(ans)
                        phoneNumberEditText?.editText?.setSelection(phoneNumberEditText?.editText?.text!!.length - cursorComplement)
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false
                }
            }
        })
    }

    private fun setupBirthdayInputText() {

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayofMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayofMonth)
            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            birthdayEditText?.editText?.setText(sdf.format(myCalendar.time))
        }

        birthdayEditText?.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()

        }
    }

    private fun setupErrorEnabling() {
        firstNameEditText?.isErrorEnabled = true
        lastNameEditText?.isErrorEnabled = true
        middleNameEditText?.isErrorEnabled = true
        usernameEditText?.isErrorEnabled = true
        iinEditText?.isErrorEnabled = true
        phoneNumberEditText?.isErrorEnabled = true
        birthdayEditText?.isErrorEnabled = true
        passwordEditText?.isErrorEnabled = true
        passwordConfirmEditText?.isErrorEnabled = true
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
                val field = Fields()
                val specs = SpecialProperties()
                val validators = Validators()
                val errors = Errors()
                field.id = fieldData.getInt("id")
                field.title = fieldData.getString("title")
                field.description = fieldData.getString("description")
                field.isRequired = fieldData.getBoolean("is_required")
                if (fieldData.has("target_field_id")){
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
                    ){
                        errors.blanc =
                            fieldData.getJSONObject("errors").getString("blank")
                        errors.target_value_mismatch =
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
                if (fieldData.getString("type") == "input_text"){
                    field.type = Fields.InputType.INPUT_TEXT
                }else if (fieldData.getString("type") == "date_selection"){
                    field.type = Fields.InputType.DATE_SELECTION
                }else if (fieldData.getString("type") == "password"){
                    field.type = Fields.InputType.PASSWORD
                }else if (fieldData.getString("type") == "password_confirmation"){
                    field.type = Fields.InputType.PASSWORD_CONFIRMATION
                }

                fields.add(field)


            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val form = Form(fields)
        val formAdapter = FormAdapter(form)
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


}