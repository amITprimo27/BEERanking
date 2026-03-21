package com.example.beeranking.features.beer.shared.beerSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeranking.databinding.DialogBeerSearchBinding

class BeerSearchDialogFragment : DialogFragment() {

    private var binding: DialogBeerSearchBinding? = null
    private val viewModel: BeerSearchViewModel by activityViewModels()
    private var beerSearchAdapter: BeerSearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogBeerSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        beerSearchAdapter = BeerSearchAdapter(emptyList()) { beer ->
            viewModel.selectBeer(beer)
            dismiss()
        }

        binding?.beerSearchRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = beerSearchAdapter
        }

        binding?.beerSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchBeers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { 
                    if (it.length > 2) { // Only search when the user has typed a few characters
                        viewModel.searchBeers(it) 
                    }
                }
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) {
            beerSearchAdapter?.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearSearch()
        binding = null
    }
}
