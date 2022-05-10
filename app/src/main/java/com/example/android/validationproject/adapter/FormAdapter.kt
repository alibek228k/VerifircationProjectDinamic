package com.example.android.validationproject.adapter

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.R
import com.example.android.validationproject.modal.Fields
import com.example.android.validationproject.modal.Form
import com.google.android.material.textfield.TextInputLayout


class FormAdapter(
    var form: Form
): RecyclerView.Adapter<FormAdapter.FormViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.input_layout, parent, false)
        return FormViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {



        val fields = form.fields[position]
        holder.title.hint = form.fields[position].title

        // description configuration
        if (!(fields.description.equals("null"))){
            holder.description.text = fields.description
        }else{
            holder.description.text = ""
        }
        // type configuration
        when (fields.type){
            Fields.InputType.INPUT_TEXT -> holder.title.editText?.inputType = InputType.TYPE_CLASS_TEXT
            Fields.InputType.DATE_SELECTION -> holder.title.editText?.inputType = InputType.TYPE_NULL
            Fields.InputType.PASSWORD -> holder.title.editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            else -> {
                holder.title.editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }


    }

    override fun getItemCount(): Int {
        return form.fields.size
    }

    class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        var description: TextView = itemView.findViewById(R.id.fieldDescription)
    }

}