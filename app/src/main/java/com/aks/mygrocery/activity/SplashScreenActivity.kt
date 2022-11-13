package com.aks.mygrocery.activity

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.aks.mygrocery.R
import com.aks.mygrocery.base.BaseActivity
import com.aks.mygrocery.databinding.ActivitySplashScreenBinding
import com.aks.mygrocery.utils.Constants
import com.aks.mygrocery.utils.SharedPreference

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashScreenBinding
    private lateinit var sharedPreferences: SharedPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash_screen)
        sharedPreferences = SharedPreference(applicationContext)
        binding.splashLottie.addAnimatorListener(animatorListener)
    }
    private val animatorListener = object :AnimatorListener {
        override fun onAnimationStart(p0: Animator?) {
            Log.d("TAG", "onAnimationStart")
        }

        override fun onAnimationEnd(p0: Animator?) {
            if (sharedPreferences.fetchBooleanDataFromSharedPref(Constants.IS_LOGIN_DONE)==true) {
                startActivity(Intent(this@SplashScreenActivity, OnBoardingActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashScreenActivity, OnBoardingActivity::class.java))
                finish()
            }
        }

        override fun onAnimationCancel(p0: Animator?) {
            Log.d("TAG", "onAnimationCancel")
        }

        override fun onAnimationRepeat(p0: Animator?) {
            Log.d("TAG", "onAnimationRepeat:")
        }

    }
}