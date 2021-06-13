package com.begoml.app.presentation.loginmvvm

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.begoml.app.R
import com.begoml.app.databinding.FragmentLoginMvvmBinding
import com.begoml.app.di.AppComponent
import com.begoml.app.presentation.loginmvvm.LoginMvvmViewModel.ViewState
import com.begoml.app.tools.hideSoftKeyboard
import com.begoml.archkit.viewstate.collectEvent
import com.begoml.archkit.viewstate.render
import com.begoml.archkit.viewstate.viewStateWatcher
import javax.inject.Inject

class LoginMvvmFragment : Fragment(R.layout.fragment_login_mvvm) {

    @Inject
    lateinit var factory: LoginMvvmFactory
    private val viewModel: LoginMvvmViewModel by viewModels { factory }
    private val binding by viewBinding(FragmentLoginMvvmBinding::bind)

    private val watcher = viewStateWatcher<ViewState> {

        ViewState::isLoading {
            binding.progressBar.isVisible = it
        }
        ViewState::buttonIsEnabled {
            binding.buttonSend.isEnabled = it
        }
        ViewState::loginState { emailState ->
            binding.inputLogin.renderView(emailState)
        }
        ViewState::passwordState { passwordState ->
            binding.inputPass.renderView(passwordState)
        }
    }
    private val userLogin: String
        get() = binding.inputLogin.text

    private val userPassword: String
        get() = binding.inputPass.text

    init {
        AppComponent.get().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.render(
            lifecycleOwner = viewLifecycleOwner,
            watcher = watcher
        )
        viewModel.collectEvent(lifecycle) { event ->
            return@collectEvent when (event) {
                is LoginMvvmViewModel.Event.UserIsLoginIn -> {
                    navigateToMainScreen()
                }
            }
        }
        with(binding){
            inputLogin.apply {
                onClickKeyboardDoneButton {
                    viewModel.onLoginFocusChanged(userLogin)
                }
                onEditTextChangeFocus {
                    viewModel.onLoginFocusChanged(userLogin)
                }
            }
            inputPass.apply {
                onClickKeyboardDoneButton {
                    viewModel.onUserLoginFocusChanged(userPassword)
                }
                onEditTextChangeFocus {
                    viewModel.onUserLoginFocusChanged(userPassword)
                }
            }
            buttonSend.setOnClickListener {
                it.hideSoftKeyboard()
                viewModel.onRegisterUserButtonClicked()
            }
        }
    }

    private fun navigateToMainScreen() {
        findNavController().popBackStack()
    }
}
