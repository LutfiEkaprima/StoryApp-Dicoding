package com.dicoding.picodiploma.loginwithanimation.view.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityHomeBinding
import com.dicoding.picodiploma.loginwithanimation.view.viewmodel.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.home.upload.UploadStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.MainActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var storyAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabUpload.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }


        lifecycleScope.launch {
            val factory = ViewModelFactory.getInstance(this@HomeActivity)
            viewModel = ViewModelProvider(this@HomeActivity, factory)[HomeViewModel::class.java]
            viewModel.listStoryItem.observe(this@HomeActivity) { story ->
                if (story != null) {
                    storyAdapter.submitList(story.listStory)
                } else {
                    Toast.makeText(this@HomeActivity, getString(R.string.data_validty), Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.isLoading.observe(this@HomeActivity) { isLoading ->
                showLoading(isLoading)
            }

            setupRecycleView()
            viewModel.findListStoryItem()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                Toast.makeText(this, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecycleView() {
        storyAdapter = HomeAdapter { isLoading ->
            showLoading(isLoading)
        }
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
}