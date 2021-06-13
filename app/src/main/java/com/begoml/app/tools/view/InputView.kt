package com.begoml.app.tools.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.begoml.app.R
import com.begoml.app.tools.hideSoftKeyboard
import com.begoml.app.tools.restoreChildViewStates
import com.begoml.app.tools.saveChildViewStates
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@SuppressLint("CustomViewStyleable")
class InputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {

        private const val SPARSE_STATE_KEY = "SPARSE_STATE_KEY"
        private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
        private const val DEFAULT_INPUT_RADIUS = 25f
    }

    private var textFooter: TextView
    private var txtHeader: TextView
    private var txtHeaderRight: TextView
    private var inputEditText: TextInputEditText
    private val inputLayout: TextInputLayout

    sealed class InputViewState {
        object DefaultState : InputViewState()
        data class TextChangeState(
            val inputText: String?
        ) : InputViewState()

        data class ErrorState(
            val messageFooter: String?,
            @ColorRes val colorError: Int?,
            @ColorRes val colorFooter: Int?
        ) : InputViewState()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.input_view, this, true)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.InputView)
        try {
            txtHeader = findViewById(R.id.text_header_hint)
            txtHeader.apply {
                textSize =
                    attributes.getDimensionPixelSize(R.styleable.InputView_header_text_size, 16)
                        .toFloat()
                text = attributes.getString(R.styleable.InputView_text_header)
                isAllCaps = attributes.getBoolean(R.styleable.InputView_header_text_all_caps, false)
            }
            txtHeaderRight = findViewById(R.id.text_header_hint_right)
            txtHeaderRight.apply {
                text = attributes.getString(R.styleable.InputView_text_header_right)
                setTextColor(
                    attributes.getInt(
                        R.styleable.InputView_color_text_header_right,
                        getColor(context, R.color.gray)
                    )
                )
            }
            inputLayout = findViewById(R.id.user_inputLayout)
            inputLayout.apply {
                endIconMode = attributes.getInt(R.styleable.InputView_toggle_enabled, 0)
                setBoxCornerRadii(
                    DEFAULT_INPUT_RADIUS,
                    attributes.getDimension(
                        R.styleable.InputView_input_boxCornerRadiusTopEnd,
                        DEFAULT_INPUT_RADIUS
                    ),
                    DEFAULT_INPUT_RADIUS,
                    attributes.getDimension(
                        R.styleable.InputView_input_boxCornerRadiusBottomEnd,
                        DEFAULT_INPUT_RADIUS
                    ),
                )
            }
            inputEditText = findViewById(R.id.password_edit_text)
            inputEditText.apply {
                hint = attributes.getString(R.styleable.InputView_text_hint)
                inputType = attributes.getType(R.styleable.InputView_android_inputType)
            }
            textFooter = findViewById(R.id.text_footer)
            textFooter.text = attributes.getString(R.styleable.InputView_text_footer)
        } finally {
            attributes.recycle()
        }
    }

    fun setHint(textHint: String) {
        inputEditText.hint = textHint
    }

    fun onClickKeyboardDoneButton(onDoneClick: () -> Unit) {
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    onDoneClick.invoke()
                    inputEditText.hideSoftKeyboard()
                    true
                }
                else -> false
            }
        }
    }

    fun onEditTextChangeFocus(onFocusChangeClick: () -> Unit) {
        inputEditText.onFocusChangeListener =
            OnFocusChangeListener { _, focusOn ->
                if (!focusOn) {
                    onFocusChangeClick.invoke()
                }
            }
    }

    var text: String
        get() = inputEditText.text.toString()
        set(value) {
            inputEditText.setText(value)
        }

    fun renderView(viewState: InputViewState): Unit? {
        return when (viewState) {
            is InputViewState.DefaultState -> {
                textFooter.setTextColor(getColor(context, R.color.gray))
            }
            is InputViewState.ErrorState -> {
                textFooter.text = viewState.messageFooter
                viewState.colorError?.let {
                    inputLayout.boxStrokeColor = it
                }
                viewState.colorFooter?.let {
                    textFooter.setTextColor(it)
                }
            }
            is InputViewState.TextChangeState -> {
                text = viewState.inputText.toString()
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
            putSparseParcelableArray(SPARSE_STATE_KEY, saveChildViewStates())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        when (newState) {
            is Bundle -> {
                val childrenState = newState.getSparseParcelableArray<Parcelable>(SPARSE_STATE_KEY)
                childrenState?.let { restoreChildViewStates(it) }
                newState = newState.getParcelable(SUPER_STATE_KEY)
            }
        }
        super.onRestoreInstanceState(newState)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }
}
