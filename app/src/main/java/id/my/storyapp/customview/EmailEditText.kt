package id.my.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import id.my.storyapp.R

class EmailEditText : TextInputEditText {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        val emailPattern = context.getString(R.string.email_pattern)

        if (!text.toString().matches(emailPattern.toRegex())) {
            setError(context.getString(R.string.email_error), null)
        } else {
            error = null
        }
    }
}