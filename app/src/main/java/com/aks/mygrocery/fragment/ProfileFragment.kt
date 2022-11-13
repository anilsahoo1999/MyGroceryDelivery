package com.aks.mygrocery.fragment

import android.os.Bundle
import android.view.View
import com.aks.mygrocery.R
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentProfileBinding
import com.aks.mygrocery.utils.Constants
import com.aks.mygrocery.utils.SharedPreference
import com.bumptech.glide.Glide


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun getFragmentId(): Int {
        return R.layout.fragment_profile
    }

    private lateinit var sharedPreference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {

        sharedPreference = SharedPreference(requireActivity())
        binding.txtName.text = sharedPreference.fetchDetailsFromSharedPref(Constants.USERNAME)
        binding.txtEmail.text = sharedPreference.fetchDetailsFromSharedPref(Constants.USER_MAIL_ID)

        Glide.with(binding.profileImage.context)
            .load(sharedPreference.fetchDetailsFromSharedPref(Constants.PROFILE_LINK))
            .into(binding.profileImage)

    }

}