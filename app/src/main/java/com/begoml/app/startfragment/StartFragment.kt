package com.begoml.app.startfragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.begoml.app.startfragment.StartFragmentViewModel.*
import com.begoml.archkit.viewstate.collectEvent
import com.begoml.archkit.viewstate.render
import com.begoml.archkit.viewstate.viewStateWatcher

class StartFragment : Fragment() {

    private val viewModel: StartFragmentViewModel by viewModels()

    private val watcher = viewStateWatcher<ViewState> {
        ViewState::isDataLoading {

        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToNextScreen()
        viewModel.render(
            lifecycleOwner = viewLifecycleOwner,
            watcher = watcher
        )
        viewModel.collectEvent(
            lifecycle
        ) { event ->
            return@collectEvent when (event){
                Event.NavigateToNextScreen -> {
                    goToNextScreen()
                }
            }
        }
    }

    private fun goToNextScreen() {

    }
}
