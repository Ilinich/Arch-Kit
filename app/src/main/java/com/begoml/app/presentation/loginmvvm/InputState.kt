package com.begoml.app.presentation.loginmvvm

import com.begoml.app.tools.Const
import com.begoml.app.tools.view.InputView

enum class InputState {

    DEFAULT_STATE {
        override fun getState(
            messageFooter: String?,
            colorError: Int?,
            colorFooter: Int?
        ): InputView.InputViewState = InputView.InputViewState.DefaultState
    },
    ERROR_STATE {
        override fun getState(messageFooter: String?, colorError: Int?, colorFooter: Int?): InputView.InputViewState =
            InputView.InputViewState.ErrorState(
                messageFooter = null,
                colorError = colorError,
                colorFooter = colorFooter
            )
    };

    abstract fun getState(
        messageFooter: String? = Const.EMPTY_STRING, colorError: Int? = 0, colorFooter: Int? = 0): InputView.InputViewState
}
