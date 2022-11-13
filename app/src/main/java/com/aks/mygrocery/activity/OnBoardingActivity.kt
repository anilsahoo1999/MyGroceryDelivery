package com.aks.mygrocery.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.aks.mygrocery.R
import com.aks.mygrocery.adapter.OnBoardingViewPagerAdapter
import com.aks.mygrocery.databinding.ActivityOnBoardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingActivity : AppCompatActivity() {
    lateinit var binding : ActivityOnBoardingBinding
    lateinit var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_on_boarding)

        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this,this)
        binding.viewPager.adapter = onBoardingViewPagerAdapter

        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()

        if (binding.viewPager.currentItem == 0) {
            binding.btnPreviousStep.visibility = View.GONE
        }
        binding.btnNextStep.setOnClickListener {
            if (binding.viewPager.currentItem > binding.viewPager.childCount) {
//                finish()
                //startActivityLogin
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            } else {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }
        }

        binding.btnPreviousStep.setOnClickListener {
            if (binding.viewPager.currentItem == 0) {
                finish()
            } else {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position == 0) {
                    binding.btnPreviousStep.visibility = View.GONE
                    binding.btnNextStep.text = "Next"
                }
                if (position == 1) {
                    if (!binding.btnPreviousStep.isVisible) {
                        binding.btnPreviousStep.visibility = View.VISIBLE
                    }
                    binding.btnNextStep.text = "Next"
                }
                if (position == 2) {
                    binding.btnNextStep.text = "Finish"
                }
            }
        })
    }
}