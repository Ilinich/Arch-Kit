package com.begoml.app.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.begoml.app.R
import com.begoml.app.databinding.FragmentProfileBinding
import com.begoml.app.di.AppComponent
import com.begoml.app.presentation.profile.ProfileViewModel.*
import com.begoml.app.presentation.profile.adapter.ProfileAdapter
import com.begoml.app.tools.loadImage
import com.begoml.app.tools.setDivider
import com.begoml.archkit.viewstate.collectEvent
import com.begoml.archkit.viewstate.render
import com.begoml.archkit.viewstate.viewStateWatcher
import javax.inject.Inject

class ProfileFragment: Fragment(R.layout.fragment_profile){

    private val binding by viewBinding(FragmentProfileBinding::bind)
    @Inject
    lateinit var factory: ProfileFactory
    private val viewModel : ProfileViewModel by viewModels{factory}

    private val movieAdapter by lazy {
        ProfileAdapter()
    }
    private val watcher = viewStateWatcher<ViewState> {
        ViewState::userName {
            binding.itemProfile.textName.text = it
        }
        ViewState::userInfo {
            binding.itemProfile.textUserInfo.text = it
        }
        ViewState::bornDate {
            binding.itemProfile.textBorn.text = it
        }
        ViewState::education {
            binding.itemProfile.textEducation.text = it
        }
        ViewState::movieList {
            movieAdapter.submitList(it)
        }
        ViewState::imageUrl {
            binding.itemProfile.imageProfile.loadImage(it)
        }
    }

    init {
        AppComponent.get().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            listProfile.apply{
                adapter = movieAdapter
                setDivider(R.drawable.recycler_view_divider)
            }
        }
        viewModel.render(
            lifecycleOwner = viewLifecycleOwner,
            watcher = watcher
        )
    }
}
