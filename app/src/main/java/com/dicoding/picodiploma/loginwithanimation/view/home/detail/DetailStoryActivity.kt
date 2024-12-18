package com.dicoding.picodiploma.loginwithanimation.view.home.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val itemDataList = "list_story"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportPostponeEnterTransition()

        @Suppress("DEPRECATION")
        val storyItem: ListStoryItem? = intent.getParcelableExtra(itemDataList)
        if (storyItem != null) {
            showLoading(true)
            binding.tvDetailName.text = storyItem.name
            binding.tvStoryTime.text = storyItem.createdAt
            binding.tvDetailDescription.text = storyItem.description

            Glide.with(this)
                .load(storyItem.photoUrl)
                .into(binding.ivDetailPhoto)
        } else {
            handleError()
        }

        binding.ivDetailPhoto.viewTreeObserver.addOnPreDrawListener {
            supportStartPostponedEnterTransition()
            true
        }
        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleError() {
        val message = "Data Is Missing!"
        showLoading(false)
        binding.tvDetailName.text = message
    }
}
