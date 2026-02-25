package com.example.beeranking.features.beer.shared.postList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeranking.databinding.PostRowLayoutBinding
import com.example.beeranking.model.PostWithUser

interface OnPostClickListener {
    fun onPostItemClick(postWithUser: PostWithUser)
}

class PostAdapter(
    var posts: MutableList<PostWithUser>?,
): RecyclerView.Adapter<PostRowViewHolder>() {

    var listener: OnPostClickListener? = null
    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostRowViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val binding = PostRowLayoutBinding.inflate(inflator, parent, false)
        return PostRowViewHolder(
            binding = binding,
            listener = listener
        )
    }

    override fun onBindViewHolder(holder: PostRowViewHolder, position: Int) {
        posts?.let {
            holder.bind(it[position], position)
        }
    }
}