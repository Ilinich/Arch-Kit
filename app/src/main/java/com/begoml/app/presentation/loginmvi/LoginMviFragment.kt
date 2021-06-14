package com.begoml.app.presentation.loginmvi

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
import com.begoml.app.tools.hideSoftKeyboard
import com.begoml.archkit.viewstate.collectEvent
import com.begoml.archkit.viewstate.render
import com.begoml.archkit.viewstate.viewStateWatcher
import com.begoml.app.presentation.loginmvi.LoginMviViewModel.*
import com.begoml.app.tools.navigateSafe
import javax.inject.Inject

class LoginMviFragment : Fragment(R.layout.fragment_login_mvvm) {

    @Inject
    lateinit var factory: LoginMviFactory
    private val viewModel: LoginMviViewModel by viewModels { factory }
    private val binding by viewBinding(FragmentLoginMvvmBinding::bind)

    private val watcher = viewStateWatcher<ViewState> {

        ViewState::isLoading {
            binding.progressBar.isVisible = it
        }
        ViewState::buttonIsEnabled {
            binding.buttonSend.isEnabled = it
        }
        ViewState::loginState {
            binding.inputLogin.renderView(it)
        }
        ViewState::passwordState {
            binding.inputPass.renderView(it)
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
                is News.GoToProfile -> {
                    findNavController().navigateSafe(R.id.loginMviFragment) {
                        navigate(R.id.globalToProfileFragment)
                    }
                }
            }
        }
        with(binding){
            inputLogin.apply {
                onClickKeyboardDoneButton {
                    viewModel.dispatchEvent(Event.OnLoginChanged(userLogin))
                }
                onEditTextChangeFocus {
                    viewModel.dispatchEvent(Event.OnLoginChanged(userLogin))
                }
            }
            inputPass.apply {
                onClickKeyboardDoneButton {
                     viewModel.dispatchEvent(Event.OnPasswordChanged(userPassword))
                }
                onEditTextChangeFocus {
                    viewModel.dispatchEvent(Event.OnPasswordChanged(userPassword))
                }
            }
            buttonSend.setOnClickListener {
                it.hideSoftKeyboard()
                viewModel.dispatchEvent(Event.OnLoginClicked)
            }
        }
    }
}
