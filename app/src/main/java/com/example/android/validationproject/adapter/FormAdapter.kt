package com.example.android.validationproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.R
import com.example.android.validationproject.modal.Form
import com.google.android.material.textfield.TextInputLayout


class FormAdapter(
    var title: ArrayList<Form>
): RecyclerView.Adapter<FormAdapter.FormViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.input_layout, parent, false)
        return FormViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        title. = 50
        holder.title.hint = title?.get(position)
        if (!(description?.get(position).equals("null"))){
            holder.description.text = description?.get(position)
        }else{
            holder.description.text = ""
        }

    }

    override fun getItemCount(): Int {
        return title?.size ?: 0
    }

    class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        var description: TextView = itemView.findViewById(R.id.fieldDescription)
    }

}