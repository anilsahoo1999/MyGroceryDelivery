package com.aks.mygrocery.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aks.mygrocery.R
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentOrderBinding

class OrderFragment : BaseFragment<FragmentOrderBinding>() {
    override fun getFragmentId(): Int {
        return R.layout.fragment_order
    }

}