package com.example.android.validationproject.adapter

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.R
import com.example.android.validationproject.Validation.EditTextValidator
import com.example.android.validationproject.data.SpecialProperties
import com.example.android.validationproject.model.Field
import com.google.android.material.textfield.TextInputLayout


class FormAdapter(
    private val fields: List<Field>,
    private val listener: Listener
) : RecyclerView.Adapter<FormAdapter.BaseViewHolder>() {

    override fun getItemCount(): Int {
        return fields.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (fields[position].type) {
            Field.Type.INPUT_TEXT -> 666
            Field.Type.DATE_SELECTION -> 777
            Field.Type.PASSWORD -> 888
            Field.Type.PASSWORD_CONFIRMATION -> 999
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            666 -> InputTextViewHolder(layoutInflater.inflate(R.layout.input_layout, parent, false))
            777 -> DateSelectionViewHolder(
                layoutInflater.inflate(
                    R.layout.input_layout,
                    parent,
                    false
                )
            )
            888 -> PasswordViewHolder(layoutInflater.inflate(R.layout.input_layout, parent, false))
            999 -> PasswordConfirmationViewHolder(
                layoutInflater.inflate(
                    R.layout.input_layout,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is InputTextViewHolder -> holder.bind(fields[position])
            is DateSelectionViewHolder -> holder.bind(fields[position])
            is PasswordViewHolder -> holder.bind(fields[position])
            is PasswordConfirmationViewHolder -> holder.bind(fields[position])
        }

    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.lastOrNull()

        if (payload is Bundle) {
            if (payload.getString("action") == "validation") {
                val message = payload.getString("message")
                val success = payload.getBoolean("success")

                if (holder is InputTextViewHolder) {
                    holder.validate(message, success)
                }
                if (holder is PasswordViewHolder){
                    holder.validate(message, success)
                }
                if (holder is PasswordConfirmationViewHolder){
                    holder.validate(message, success)
                }
                if (holder is DateSelectionViewHolder){
                    holder.validate(message, success)
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun onValidate(position: Int, s: String, ss: Boolean) {
        notifyItemChanged(
            position,
            bundleOf("action" to "validation", "message" to s, "success" to ss)
        )
    }
    fun errorChecking(){

    }

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        protected fun TextView.setDescription(description: String?) {
            if (description.equals("null")) {
                text = null
                visibility = View.GONE
            } else {
                text = description
                visibility = View.VISIBLE
            }
        }

        protected fun TextInputLayout.setSpecs(spec: SpecialProperties) {
            if (spec.inputMaxLength != null && spec.inputMaxLines != null) {
                this.editText?.maxWidth = spec.inputMaxLength!!
                this.editText?.maxLines = spec.inputMaxLines!!
            } else if (spec.inputMaxLength != null) {
                this.editText?.maxWidth = spec.inputMaxLength!!
            } else if (spec.inputMaxLines != null) {
                this.editText?.maxLines = spec.inputMaxLines!!
            }
        }

        protected fun TextInputLayout.validator(field: Field) {
            editText?.addTextChangedListener(object : EditTextValidator {
                override fun validate(text: String?) {
                    listener.onTextChanged(field, adapterPosition, text)
                }
            })
        }

        protected fun TextInputLayout.datePickerClicked(formInput: TextInputLayout){
            listener.onDateInputClicked(formInput)
        }
    }

    private inner class InputTextViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(field: Field) {
            formInput.hint = field.title

            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.editText?.inputType = InputType.TYPE_CLASS_TEXT
            formInput.validator(field)
        }

        fun validate(message: String?, success: Boolean) {
            if (success) {
                formInput.error = null
            } else {
                formInput.error = message
            }
        }

    }

    private inner class PasswordViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(field: Field) {
            formInput.hint = field.title
            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.validator(field)
            formInput.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            formInput.errorIconDrawable = null
            formInput.setStartIconDrawable(R.drawable.ic_password)
        }
        fun validate(message: String?, success: Boolean) {
            if (success) {
                formInput.error = null
            } else {
                formInput.error = message
            }
        }

    }

    private inner class PasswordConfirmationViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(field: Field) {
            formInput.hint = field.title
            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.validator(field)
            formInput.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            formInput.errorIconDrawable = null
            formInput.setStartIconDrawable(R.drawable.ic_password)
        }
        fun validate(message: String?, success: Boolean) {
            if (success) {
                formInput.error = null
            } else {
                formInput.error = message
            }
        }

    }

    private inner class DateSelectionViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(field: Field) {
            formInput.hint = field.title
            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            if (field.isRequired == true) {
                formInput.validator(field)
            }
            formInput.editText?.inputType = InputType.TYPE_NULL
            formInput.editText?.isFocusable = false
            formInput.editText?.isCursorVisible = false
            formInput.setStartIconDrawable(R.drawable.ic_calendar)
            formInput.datePickerClicked(formInput)
        }
        fun validate(message: String?, success: Boolean) {
            if (success) {
                formInput.error = null
            } else {
                formInput.error = message
            }
        }

    }

    interface Listener {
        fun onTextChanged(field: Field, position: Int, value: String?)
        fun onDateInputClicked(formInput: TextInputLayout)
    }

}