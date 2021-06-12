package com.begoml.app.startfragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.begoml.app.R
import com.begoml.app.databinding.FragmentStartBinding
import com.begoml.app.startfragment.StartFragmentViewModel.*
import com.begoml.archkit.viewstate.collectEvent

class StartFragment : Fragment(R.layout.fragment_start) {

    private val binding by viewBinding(FragmentStartBinding::bind)
    private val viewModel: StartFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.collectEvent(
            lifecycle
        ) { event ->
            return@collectEvent when (event){
                Event.NavigateToMvvmScreen -> {
                    goToMvvmScreen()
                }
                Event.NavigateToProfileScreen -> {
                    goToProfileScreen()
                }
                Event.NavigateToMviScreen -> {
                    goToMviScreen()
                }
            }
        }
        with(binding){
            btnGoLoginMvvm.setOnClickListener {
                viewModel.onBtnMvvmClicked()
            }
            btnGoProfile.setOnClickListener {
                viewModel.onBtnProfileClicked()
            }
            btnGoLoginMvi.setOnClickListener {
                viewModel.onBtnMviClicked()
            }
        }
    }

    private fun goToMvvmScreen() {
        findNavController().navigate(R.id.actionToLoginMvvmFragment)
    }

    private fun goToProfileScreen() {
        findNavController().navigate(R.id.actionToProfileFragment)
    }

    private fun goToMviScreen() {
        findNavController().navigate(R.id.actionLoginMviFragment)
    }
}
