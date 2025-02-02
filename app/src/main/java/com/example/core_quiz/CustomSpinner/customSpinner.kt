package com.example.core_quiz.CustomSpinner

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.core_quiz.R

class customSpinner : AppCompatAutoCompleteTextView {
    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    override fun enoughToFilter(): Boolean {
        return true
    }

    private fun initialize() {
        // Set text size and handle clicks to show dropdown
        textSize = 14F
        setOnClickListener {
            if (!isPopupShowing) showDropDown()
        }

        // Set up the clear button and its behavior
        setClearButton()
    }

    /**
     * Sets up the clear button and its behavior.
     */
    private fun setClearButton() {
        // Add a touch listener to detect clicks on the clear button
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= right - totalPaddingRight) {
                    text = null
                    if (!isPopupShowing) showDropDown()
                    onClear?.invoke()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Add a text change listener to toggle the clear button icon
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateClearButton()
            }
        })
    }

    /**
     * Updates the clear button icon based on whether text is present in the spinner.
     * Custom drawables for the dropdown and close icons should be used.
     */
    private fun updateClearButton() {
        // Determine the drawable icon based on whether text is present
        val drawable = if (text.isNullOrEmpty()) {
            // Use your custom drawable for the dropdown icon
            R.drawable.arrow_down
        } else {
            // Use your custom drawable for the close icon
            R.drawable.baseline_close_24
        }
        // Set the drawable icon to the right of the text
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawable, 0)
    }

    /**
     * Clears the selected text in the spinner.
     */
    fun clearSelectedText() {
        setText("")
    }

    private var onClear: (() -> Unit)? = null

    /**
     * Sets a callback for when the clear button is clicked.
     */
    fun onClearClick(onClear: () -> Unit) {
        this.onClear = onClear
    }
}