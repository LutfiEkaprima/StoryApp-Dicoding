package com.dicoding.picodiploma.loginwithanimation.view.home

import androidx.recyclerview.widget.ListAdapter
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.home.detail.DetailStoryActivity


class HomeAdapter(private val onLoading: (Boolean) -> Unit)
    : ListAdapter<ListStoryItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
    inner class MyViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, onLoading: (Boolean) -> Unit) {
            binding.tvItemName.text = story.name
            Glide.with(binding.imgItemPhoto.context)
                .load(story.photoUrl)
                .into(binding.imgItemPhoto)

            val itemDataList = "list_story"
            binding.cardView.setOnClickListener {
                onLoading(true)
                val context = itemView.context
                val intent = Intent(context, DetailStoryActivity::class.java)
                intent.putExtra(itemDataList, story)
                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    Pair(binding.imgItemPhoto, "image"),
                    Pair(binding.tvItemName, "name")
                )
                context.startActivity(intent, optionsCompat.toBundle())
                onLoading(false)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), onLoading)
    }


}