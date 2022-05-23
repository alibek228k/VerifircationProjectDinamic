package com.example.android.validationproject.adapter

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.validationproject.R
import com.example.android.validationproject.Validation.EditTextValidator
import com.example.android.validationproject.data.SpecialProperties
import com.example.android.validationproject.model.Field
import com.example.android.validationproject.model.FieldPrompt
import com.google.android.material.textfield.TextInputLayout


class FormAdapter constructor(
    private val listener: Listener
) : RecyclerView.Adapter<FormAdapter.BaseViewHolder>() {
    var hashMap = HashMap<Int, Int>()
    var fieldPrompts: List<FieldPrompt> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return fieldPrompts.size
    }


    override fun getItemViewType(position: Int): Int {
        hashMap[position] = position
        return when (fieldPrompts[position].field.type) {
            Field.Type.INPUT_TEXT -> 666
            Field.Type.DATE_SELECTION -> 777
            Field.Type.PASSWORD -> 888
            Field.Type.PASSWORD_CONFIRMATION -> 999
            else -> -1
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val position = hashMap[hashMap.size-1]

        Log.d("ALIBEK", "Position: $position, Size: ${hashMap.size}")
        return when (viewType) {
            666 -> InputTextViewHolder(
                layoutInflater.inflate(R.layout.input_layout, parent, false),
                EditTextValidator {
                    listener.onTextChanged(
                        fieldPrompts[position!!].field,
                        position,
                        it
                    )
                })
            777 -> DateSelectionViewHolder(
                layoutInflater.inflate(
                    R.layout.input_layout,
                    parent,
                    false
                ), EditTextValidator {
                    listener.onTextChanged(
                        fieldPrompts[position!!].field,
                        position,
                        it
                    )
                }
            )
            888 -> PasswordViewHolder(
                layoutInflater.inflate(R.layout.input_layout, parent, false),
                EditTextValidator {
                    listener.onTextChanged(
                        fieldPrompts[position!!].field,
                        position,
                        it
                    )
                })
            999 -> PasswordConfirmationViewHolder(
                layoutInflater.inflate(
                    R.layout.input_layout,
                    parent,
                    false
                ), EditTextValidator {
                    listener.onTextChanged(
                        fieldPrompts[position!!].field,
                        position,
                        it
                    )
                }
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val fieldPrompt = fieldPrompts[position]
        when (holder) {
            is InputTextViewHolder -> holder.bind(fieldPrompt)
            is DateSelectionViewHolder -> holder.bind(fieldPrompt)
            is PasswordViewHolder -> holder.bind(fieldPrompt)
            is PasswordConfirmationViewHolder -> holder.bind(fieldPrompt)
        }

    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        when(holder){
            is InputTextViewHolder -> holder.addToTextWatcher()
            is DateSelectionViewHolder -> holder.addToTextWatcher()
            is PasswordViewHolder -> holder.addToTextWatcher()
            is PasswordConfirmationViewHolder -> holder.addToTextWatcher()
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        when(holder){
            is InputTextViewHolder -> holder.disableTextWatcher()
            is DateSelectionViewHolder -> holder.disableTextWatcher()
            is PasswordViewHolder -> holder.disableTextWatcher()
            is PasswordConfirmationViewHolder -> holder.disableTextWatcher()
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

                when (holder) {
                    is InputTextViewHolder -> {
                        holder.validate(position, message, success)
                    }
                    is PasswordViewHolder -> {
                        holder.validate(position, message, success)
                    }
                    is PasswordConfirmationViewHolder -> {
                        holder.validate(position, message, success)
                    }
                    is DateSelectionViewHolder -> {
                        holder.validate(position, message, success)
                    }
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


    abstract inner class BaseViewHolder(
        itemView: View,
        myCustomEditTextListener: EditTextValidator
    ) : RecyclerView.ViewHolder(itemView) {
        private val editTextListener = myCustomEditTextListener
        protected fun TextView.setDescription(description: String?) {
            if (description.equals("null") || description.isNullOrBlank()) {
                text = null
                visibility = View.GONE
            } else {
                text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                setLinkTextColor(ContextCompat.getColor(itemView.context, R.color.purple_500))
                movementMethod = LinkMovementMethod.getInstance()
                visibility = View.VISIBLE
            }
        }

        protected fun TextInputLayout.setSpecs(spec: SpecialProperties) {
            val inputMaxLength = spec.inputMaxLength
            val inputMaxLines = spec.inputMaxLines
            if (inputMaxLength != null && inputMaxLines != null) {
                editText?.maxWidth = inputMaxLength
                editText?.maxLines = inputMaxLines
            } else if (inputMaxLength != null) {
                editText?.maxWidth = inputMaxLength
            } else if (inputMaxLines != null) {
                editText?.maxLines = inputMaxLines
            }
        }

        protected fun TextInputLayout.validator() {
            editText?.addTextChangedListener(editTextListener)
        }
        fun TextInputLayout.disableTextWatcher(){
            editText?.removeTextChangedListener(editTextListener)
        }


        protected fun TextInputLayout.datePickerClicked(formInput: TextInputLayout) {
            listener.onDateInputClicked(formInput)
        }
    }

    private inner class InputTextViewHolder(
        itemView: View,
        myCustomEditTextListener: EditTextValidator
    ) : BaseViewHolder(itemView, myCustomEditTextListener) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(fieldPrompt: FieldPrompt) {
            bind(fieldPrompt.field)

            val position = fieldPrompt.field.id!!.minus(1)

            validate(position, fieldPrompt.errors, fieldPrompt.errors == "success")
        }

        fun bind(field: Field) {
            formInput.hint = field.title

            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.editText?.inputType = InputType.TYPE_CLASS_TEXT
        }
        fun disableTextWatcher(){
            formInput.disableTextWatcher()
        }
        fun addToTextWatcher(){
            formInput.validator()
        }

        fun validate(position: Int, message: String?, success: Boolean) {
            if (fieldPrompts[position].errors == message) {
                if (success) {
                    formInput.error = null
                } else {
                    formInput.error = message
                }
            }
        }

    }

    private inner class PasswordViewHolder(
        itemView: View,
        myCustomEditTextListener: EditTextValidator
    ) : BaseViewHolder(itemView, myCustomEditTextListener) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(fieldPrompt: FieldPrompt) {
            bind(fieldPrompt.field)

            val position = fieldPrompt.field.id!!.minus(1)

            validate(position, fieldPrompt.errors, fieldPrompt.errors == "success")
        }

        fun bind(field: Field) {
            formInput.hint = field.title
            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            formInput.editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            formInput.errorIconDrawable = null
            formInput.setStartIconDrawable(R.drawable.ic_password)
        }

        fun disableTextWatcher(){
            formInput.disableTextWatcher()
        }
        fun addToTextWatcher(){
            formInput.validator()
        }

        fun validate(position: Int, message: String?, success: Boolean) {
            if (fieldPrompts[position].errors == message) {
                if (success) {
                    formInput.error = null
                } else {
                    formInput.error = message
                }
            }
        }

    }

    private inner class PasswordConfirmationViewHolder(
        itemView: View,
        myCustomEditTextListener: EditTextValidator
    ) : BaseViewHolder(itemView, myCustomEditTextListener) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(fieldPrompt: FieldPrompt) {
            bind(fieldPrompt.field)

            val position = fieldPrompt.field.id!!.minus(1)

            validate(position, fieldPrompt.errors, fieldPrompt.errors == "success")
        }

        fun bind(field: Field) {
            formInput.hint = field.title
            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            formInput.editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            formInput.errorIconDrawable = null
            formInput.setStartIconDrawable(R.drawable.ic_password)
        }
        fun disableTextWatcher(){
            formInput.disableTextWatcher()
        }
        fun addToTextWatcher(){
            formInput.validator()
        }

        fun validate(position: Int, message: String?, success: Boolean) {
            if (fieldPrompts[position].errors == message) {
                if (success) {
                    formInput.error = null
                } else {
                    formInput.error = message
                    Log.d("ALIBEK", "problem: $position")
                }
            }
        }

    }

    private inner class DateSelectionViewHolder(
        itemView: View,
        myCustomEditTextListener: EditTextValidator
    ) : BaseViewHolder(itemView, myCustomEditTextListener) {
        private val formInput: TextInputLayout = itemView.findViewById(R.id.inputLayout)
        private val description: TextView = itemView.findViewById(R.id.fieldDescription)

        fun bind(fieldPrompt: FieldPrompt) {
            bind(fieldPrompt.field)

            val position = fieldPrompt.field.id!!.minus(1)

            validate(position, fieldPrompt.errors, fieldPrompt.errors == "success")
        }

        fun bind(field: Field) {
            formInput.hint = field.title
            description.setDescription(field.description)
            if (field.specs != null) {
                formInput.setSpecs(field.specs!!)
            }
            formInput.editText?.inputType = InputType.TYPE_NULL
            formInput.editText?.isFocusable = false
            formInput.editText?.isCursorVisible = false
            formInput.setStartIconDrawable(R.drawable.ic_calendar)
            formInput.datePickerClicked(formInput)
        }

        fun disableTextWatcher(){
            formInput.disableTextWatcher()
        }
        fun addToTextWatcher(){
            formInput.validator()
        }

        fun validate(position: Int, message: String?, success: Boolean) {
            if (fieldPrompts[position].errors == message) {
                if (success) {
                    formInput.error = null
                } else {
                    formInput.error = message
                }
            }
        }

    }

    interface Listener {
        fun onTextChanged(field: Field, position: Int, value: String?)
        fun onDateInputClicked(formInput: TextInputLayout)
    }

}