package com.dicoding.submissionaplikasistoryapp.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dicoding.submissionaplikasistoryapp.databinding.ActivityWelcomeBinding
import com.dicoding.submissionaplikasistoryapp.view.login.LoginActivity
import com.dicoding.submissionaplikasistoryapp.view.signup.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        playAnimation()
        setupAction()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.btnLoginWelcome, View.ALPHA, 1f).setDuration(500)
        val signup =
            ObjectAnimator.ofFloat(binding.btnRegisterWelcome, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvTitleWelcome, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvDescWelcome, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }

    private fun setupAction() {
        binding.apply {
            btnLoginWelcome.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            }
            btnRegisterWelcome.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun setupView() {
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}