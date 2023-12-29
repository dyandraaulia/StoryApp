package id.my.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import id.my.storyapp.R

class PasswordEditText : TextInputEditText {

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

        if (text.toString().length < 8) {
            setError(context.getString(R.string.password_error), null)
        } else {
            error = null
        }
    }
}