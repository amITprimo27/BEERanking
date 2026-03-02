package com.example.beeranking.features.beer.shared.postList

import androidx.recyclerview.widget.RecyclerView
import com.example.beeranking.R
import com.example.beeranking.databinding.PostRowLayoutBinding
import com.example.beeranking.model.PostWithUser
import com.squareup.picasso.Picasso
import kotlin.math.floor
import kotlin.math.roundToInt

class PostRowViewHolder(
    private val binding: PostRowLayoutBinding,
    private val listener: OnPostClickListener?
): RecyclerView.ViewHolder(binding.root) {

    private var postWithUser: PostWithUser? = null

    init {
        itemView.setOnClickListener {
            postWithUser?.let {
                listener?.onPostItemClick(it)
            }
        }
    }

    fun bind(postWithUser: PostWithUser, position: Int) {
        this.postWithUser = postWithUser
        val post = postWithUser.post
        val user = postWithUser.user

        binding.userNameTextView.text = user?.userName ?: "Unknown User"
        binding.beerInfoTextView.text = "${post.beerName} By ${post.beerBrewery}"
        binding.ratingBar.rating = post.rating
        binding.ratingTextView.text = post.rating.toString()
        binding.postDescriptionTextView.text = post.details

        if (post.postImageUrlString.isNotEmpty()) {
            Picasso.get()
                .load(post.postImageUrlString)
                .into(binding.postImageView)
        }

        if (user?.avatarUrlString != null && user.avatarUrlString.isNotEmpty()) {
            Picasso.get()
                .load(user.avatarUrlString)
                .into(binding.userAvatarImageView)
        } else {
            Picasso.get()
                .load(R.drawable.no_pfp)
                .into(binding.userAvatarImageView)
        }

    }
}