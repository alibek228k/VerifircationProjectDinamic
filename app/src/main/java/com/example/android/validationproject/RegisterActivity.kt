package com.example.android.validationproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.adapter.FormAdapter
import com.example.android.validationproject.model.Field
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), FormAdapter.Listener {

    private var recyclerView: RecyclerView? = null
    private var title: TextView? = null
    private var description: TextView? = null
    private var submitButton: MaterialButton? = null

    private var viewModel: RegisterViewModel? = null

    private var formAdapter: FormAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        recyclerView = findViewById(R.id.recyclerView)
        submitButton = findViewById(R.id.registerButton)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)

        setupRecyclerView()
        setupRegisterButton()

        viewModel?.descriptionAndTitleLiveData?.observe(this) {
            title?.text = it.title
            description?.text = it.description
        }

        viewModel?.resultLiveData?.observe(this) {
            if (it.value == "success") {
                formAdapter?.onValidate(
                    it.position,
                    it.value,
                    true
                )
            } else {
                formAdapter?.onValidate(
                    it.position,
                    it.value ?: "",
                    false
                )
            }
        }

        viewModel?.submitLiveData?.observe(this) {
            if (it.isTrue) {
                Toast.makeText(this, it.value, Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AnotherActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    it.value,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel?.fieldPromptsLiveData?.observe(this) {
            formAdapter?.fieldPrompts = it
        }
    }

    private fun setupRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        formAdapter = FormAdapter( this)
        recyclerView?.adapter = formAdapter
    }


    override fun onTextChanged(field: Field, position: Int, value: String?) {
        viewModel?.onTextChanged(field, position, value)
    }

    override fun onDateInputClicked(formInput: TextInputLayout) {
        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            formInput.editText?.setText(sdf.format(myCalendar.time))
        }

        formInput.editText?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()

        }
    }

    private fun setupRegisterButton() {
        submitButton?.setOnClickListener {
            viewModel?.onSubmit()
        }
    }


}