package com.begoml.app.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.begoml.app.R
import com.begoml.app.databinding.FragmentProfileBinding

class ProfileFragment: Fragment(R.layout.fragment_profile){

    private val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            listProfile.apply{

            }
        }
    }
}
