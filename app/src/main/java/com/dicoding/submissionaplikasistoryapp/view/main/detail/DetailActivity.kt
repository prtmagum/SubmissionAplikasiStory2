package com.dicoding.submissionaplikasistoryapp.view.main.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.submissionaplikasistoryapp.R
import com.dicoding.submissionaplikasistoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupData()
    }

    private fun setupData() {
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val photo = intent.getStringExtra("photo")

        binding.apply {
            tvNameDetail.text = name
            tvDescDetail.text = description

            Glide.with(this@DetailActivity)
                .load(photo)
                .fitCenter()
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                )
                .into(ivStoryDetail)
        }
    }

    private fun setupView() {
        supportActionBar?.apply {
            title = getString(R.string.story_detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}