package com.dicoding.submissionaplikasistoryapp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submissionaplikasistoryapp.databinding.ItemStoryRowBinding
import com.dicoding.submissionaplikasistoryapp.response.ListStoryItem
import com.dicoding.submissionaplikasistoryapp.view.main.detail.DetailActivity

class UnitTestAdapter : PagingDataAdapter<ListStoryItem, UnitTestAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    inner class MyViewHolder(private val binding: ItemStoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: ListStoryItem) {
            binding.tvTitleRow.text = itemName.name
            Glide.with(binding.root)
                .load(itemName.photo)
                .into(binding.ivStoryRow)
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.EXTRA_DATA, itemName.id)
                intentDetail.putExtra(DetailActivity.EXTRA_DATA, itemName.name)
                intentDetail.putExtra(DetailActivity.EXTRA_DATA, itemName.description)
                intentDetail.putExtra(DetailActivity.EXTRA_DATA, itemName.photo)
                binding.root.context.startActivity(intentDetail)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}