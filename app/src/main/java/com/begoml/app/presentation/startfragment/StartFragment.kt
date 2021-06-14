package com.begoml.app.presentation.startfragment

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.begoml.app.R
import com.begoml.app.databinding.FragmentStartBinding
import com.begoml.app.presentation.startfragment.StartFragmentViewModel.*
import com.begoml.app.tools.navigateSafe
import com.begoml.archkit.viewstate.collectEvent

class StartFragment : Fragment(R.layout.fragment_start) {

    private val binding by viewBinding(FragmentStartBinding::bind)
    private val viewModel: StartFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.collectEvent(
            lifecycle
        ) { event ->
            return@collectEvent when (event) {
                Event.NavigateToMvvmScreen -> {
                    navigateToNextScreen(R.id.actionToLoginMvvmFragment)
                }
                Event.NavigateToMviScreen -> {
                    navigateToNextScreen(R.id.actionLoginMviFragment)
                }
            }
        }
        with(binding) {
            btnGoLoginMvvm.setOnClickListener {
                viewModel.onBtnMvvmClicked()
            }
            btnGoLoginMvi.setOnClickListener {
                viewModel.onBtnMviClicked()
            }
        }
    }

    private fun navigateToNextScreen(@IdRes screenId: Int) {
        findNavController()
            .navigateSafe(R.id.startFragment) {
                navigate(screenId)
            }
    }
}
