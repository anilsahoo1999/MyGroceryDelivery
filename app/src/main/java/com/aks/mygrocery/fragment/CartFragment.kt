package com.aks.mygrocery.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aks.mygrocery.R
import com.aks.mygrocery.base.BaseActivity
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentCartBinding

class CartFragment : BaseFragment<FragmentCartBinding>() {

    override fun getFragmentId(): Int {
        return R.layout.fragment_cart
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).binding.bottomNavView.visibility = View.GONE
    }
}