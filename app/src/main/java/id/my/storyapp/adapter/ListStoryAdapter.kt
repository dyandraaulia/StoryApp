package id.my.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.my.storyapp.data.remote.response.ListStoryItem
import id.my.storyapp.databinding.ItemStoryBinding
import id.my.storyapp.util.DateFormatter

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: ListStoryItem?, clickListener: OnItemClickCallback) {
            binding.apply {
                tvName.text = result?.name
                tvDescription.text = result?.description
                dateTime.text = DateFormatter.formatDate(result?.createdAt)
            }

            Glide.with(binding.itemStory.context)
                .load(result?.photoUrl)
                .into(binding.ivStoryPhoto)

            binding.itemStory.setOnClickListener {
                clickListener.onItemClicked(result, binding)
            }

            binding.share.setOnClickListener {
                clickListener.onShareClicked(result)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story, onItemClickCallback)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onShareClicked(data: ListStoryItem?)
        fun onItemClicked(data: ListStoryItem?, binding: ItemStoryBinding)
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