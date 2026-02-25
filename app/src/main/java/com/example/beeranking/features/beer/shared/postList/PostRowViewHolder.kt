package com.example.beeranking.features.beer.shared.postList

import androidx.recyclerview.widget.RecyclerView
import com.example.beeranking.databinding.PostRowLayoutBinding
import com.example.beeranking.model.Post


class PostRowViewHolder(
    private val binding: PostRowLayoutBinding,
    private val listener: OnPostClickListener?
): RecyclerView.ViewHolder(binding.root) {

    private var post: Post? = null

    init {

        itemView.setOnClickListener {
            post?.let { post ->
                listener?.onPostItemClick(post)
            }
        }
    }

    fun bind(post: Post, position: Int) {
        this.post = post
//        binding.nameTextView.text = student.name
//        binding.idTextView.text = student.id
//        binding.checkbox.apply {
//            isChecked = student.isPresent
//            tag = position
//        }
//        Picasso
//            .get()
//            .load(student.avatarUrlString)
//            .into(binding.imageView)
    }
}