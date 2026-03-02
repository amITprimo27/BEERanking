package com.example.beeranking.features.beer.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeranking.databinding.FavoriteBeerRowBinding
import com.example.beeranking.model.Beer

class FavoriteBeerAdapter(
    private var beers: List<Beer>,
    private val onDeleteClicked: (Beer) -> Unit
) : RecyclerView.Adapter<FavoriteBeerAdapter.BeerViewHolder>() {

    private var isEditMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val binding = FavoriteBeerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = beers[position]
        holder.bind(beer, isEditMode, onDeleteClicked)
    }

    override fun getItemCount() = beers.size

    fun setEditMode(isEditing: Boolean) {
        isEditMode = isEditing
        notifyDataSetChanged()
    }

    fun updateData(newBeers: List<Beer>) {
        this.beers = newBeers
        notifyDataSetChanged()
    }

    class BeerViewHolder(private val binding: FavoriteBeerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(beer: Beer, isEditMode: Boolean, onDeleteClicked: (Beer) -> Unit) {
            binding.beerNameText.text = beer.name
            binding.brewerNameText.text = beer.brewer?.name ?: "Unknown Brewer"

            if (isEditMode) {
                binding.deleteBeerButton.visibility = View.VISIBLE
                binding.deleteBeerButton.setOnClickListener { onDeleteClicked(beer) }
            } else {
                binding.deleteBeerButton.visibility = View.GONE
                binding.deleteBeerButton.setOnClickListener(null)
            }
        }
    }
}
