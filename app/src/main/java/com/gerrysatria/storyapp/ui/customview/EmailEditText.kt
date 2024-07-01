package com.gerrysatria.storyapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.gerrysatria.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class EmailEditText : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                when {
                    inputText.isEmpty() -> setError(context.getString(R.string.error_blank), null)
                    !Patterns.EMAIL_ADDRESS.matcher(inputText).matches() -> setError(context.getString(R.string.error_email), null)
                }
            }

            override fun afterTextChanged(s: Editable) {
                val inputText = s.toString()
                when {
                    inputText.isEmpty() -> setError(context.getString(R.string.error_blank), null)
                    !Patterns.EMAIL_ADDRESS.matcher(inputText).matches() -> setError(context.getString(R.string.error_email), null)
                }
            }
        })
    }
}