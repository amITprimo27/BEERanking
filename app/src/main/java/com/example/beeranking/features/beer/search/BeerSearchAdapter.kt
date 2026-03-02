package com.example.beeranking.features.beer.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeranking.model.Beer

class BeerSearchAdapter(
    private var beers: List<Beer>,
    private val onBeerClicked: (Beer) -> Unit
) : RecyclerView.Adapter<BeerSearchAdapter.BeerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return BeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = beers[position]
        holder.text1.text = beer.name
        holder.text2.text = beer.brewer?.name ?: "Unknown Brewer"
        holder.itemView.setOnClickListener {
            onBeerClicked(beer)
        }
    }

    override fun getItemCount() = beers.size

    fun updateData(newBeers: List<Beer>) {
        this.beers = newBeers
        notifyDataSetChanged()
    }

    class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1: TextView = itemView.findViewById(android.R.id.text1)
        val text2: TextView = itemView.findViewById(android.R.id.text2)
    }
}
