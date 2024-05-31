package com.dicoding.submissionaplikasistoryapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.submissionaplikasistoryapp.R
import com.dicoding.submissionaplikasistoryapp.data.pref.UserModel
import com.dicoding.submissionaplikasistoryapp.databinding.ActivityLoginBinding
import com.dicoding.submissionaplikasistoryapp.view.ViewModelFactory
import com.dicoding.submissionaplikasistoryapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupViewModel()
        playAnimation()
        setupAction()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitleLogin, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.tv2ndtitleLogin, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmailLogin, View.ALPHA, 1f).setDuration(500)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.tlEmailLogin, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.tvPasswordLogin, View.ALPHA, 1f).setDuration(500)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.tlPasswordLogin, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, email, emailEdit, password, passwordEdit, login)
            startDelay = 500
        }.start()
    }

    private fun setupAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (edtEmailLogin.length() == 0 || edtPasswordLogin.length() == 0) {
                    edtEmailLogin.error = getString(R.string.required_field)
                    edtPasswordLogin.error = getString(R.string.required_field)
                } else if (edtEmailLogin.length() != 0 && edtPasswordLogin.length() != 0) {
                    showLoading()
                    postText()
                    showToast()
                    moveActivity()
                }
            }
        }
    }

    private fun showLoginSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun setupView() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_login)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun showToast() {
        loginViewModel.toastText.observe(this@LoginActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this@LoginActivity, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading() {
        loginViewModel.isLoading.observe(this@LoginActivity) {
            binding.pbLogin.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun postText() {
        binding.apply {
            loginViewModel.postLogin(
                edtEmailLogin.text.toString(),
                edtPasswordLogin.text.toString()
            )
        }

        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            saveSession(
                UserModel(
                    response.loginResult?.name.toString(),
                    binding.edtEmailLogin.text.toString(),
                    (response.loginResult?.token.toString()),
                    true
                )
            )
        }
    }

    private fun moveActivity() {
        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            if (!response.error) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun saveSession(session: UserModel) {
        loginViewModel.saveSession(session)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}