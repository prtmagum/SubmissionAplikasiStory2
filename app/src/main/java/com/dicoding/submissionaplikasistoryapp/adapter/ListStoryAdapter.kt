package com.dicoding.submissionaplikasistoryapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.submissionaplikasistoryapp.R
import com.dicoding.submissionaplikasistoryapp.data.di.timeStamptoString
import com.dicoding.submissionaplikasistoryapp.databinding.ItemStoryRowBinding
import com.dicoding.submissionaplikasistoryapp.response.ListStoryItem
import com.dicoding.submissionaplikasistoryapp.view.main.detail.DetailActivity

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class ListViewHolder(private val binding: ItemStoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                tvTitleRow.text = story.name
                tvDescRow.text = story.description
                tvDateRow.text = story.createdAt.timeStamptoString()
                Glide.with(itemView.context)
                    .load(story.photo)
                    .fitCenter()
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    ).into(ivStoryRow)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)

                    // Pass necessary information via intent extras
                    intent.putExtra("name", story.name)
                    intent.putExtra("description", story.description)
                    intent.putExtra("photo", story.photo)
                    intent.putExtra("createdAt", story.createdAt.timeStamptoString())

                    // Start activity with the created intent
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldStory: ListStoryItem,
                newStory: ListStoryItem
            ): Boolean {
                return oldStory == newStory
            }

            override fun areContentsTheSame(
                oldStory: ListStoryItem,
                newStory: ListStoryItem
            ): Boolean {
                return oldStory.name == newStory.name &&
                        oldStory.description == newStory.description &&
                        oldStory.photo == newStory.photo
            }
        }
    }
}