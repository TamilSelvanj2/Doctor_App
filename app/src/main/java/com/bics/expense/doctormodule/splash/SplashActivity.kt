package com.bics.expense.doctormodule.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bics.expense.doctormodule.Login.MainActivity
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {


    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        val splashTimeOut: Long = 1000
        val homeIntent = Intent(this@SplashActivity, MainActivity::class.java)

        android.os.Handler().postDelayed({
            startActivity(homeIntent)
            finish()
        }, splashTimeOut)
    }

}
