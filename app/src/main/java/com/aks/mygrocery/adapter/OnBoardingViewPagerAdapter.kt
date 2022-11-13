package com.aks.mygrocery.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aks.mygrocery.R
import com.aks.mygrocery.fragment.OnBoardingFragment

class OnBoardingViewPagerAdapter(fragmentActivity: FragmentActivity, private val context: Context) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title1),
                context.resources.getString(R.string.description1),
                R.raw.add_to_cart
            )

            1 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title2),
                context.resources.getString(R.string.description2),
                R.raw.discount
            )

            3 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title3),
                context.resources.getString(R.string.description3),
                R.raw.delivery_grocery_and_food)

            else -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title3),
                context.resources.getString(R.string.description3),
                R.raw.delivery_grocery_and_food
            )
        }
    }

}