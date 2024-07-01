package com.gerrysatria.storyapp.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gerrysatria.storyapp.data.database.StoryEntity
import com.gerrysatria.storyapp.databinding.StoryItemBinding
import com.gerrysatria.storyapp.ui.activity.DetailStoryActivity
import com.gerrysatria.storyapp.ui.activity.DetailStoryActivity.Companion.EXTRA_DATA
import com.gerrysatria.storyapp.utils.loadImage

class ListStoriesAdapter: PagingDataAdapter<StoryEntity, ListStoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val itemBinding : StoryItemBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(data : StoryEntity){
            itemBinding.ivItemPhoto.loadImage(data.photoUrl)
            itemBinding.tvItemName.text = data.name
            itemBinding.tvItemDescription.text = data.description

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailStoryActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(itemBinding.ivItemPhoto, "image"),
                        Pair(itemBinding.tvItemName, "name"),
                        Pair(itemBinding.tvItemDescription, "description"),
                    )
                intent.putExtra(EXTRA_DATA, data)
                it.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>(){
            override fun areItemsTheSame(oldItem:StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}