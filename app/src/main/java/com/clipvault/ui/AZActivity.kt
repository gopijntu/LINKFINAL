package com.clipvault.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clipvault.databinding.ActivityAzBinding
import com.clipvault.db.AppDatabase
import com.clipvault.repositories.LinkRepository
import com.clipvault.ui.adapter.LinkAdapter
import com.clipvault.viewmodels.LinkViewModel
import com.clipvault.viewmodels.LinkViewModelFactory

class AZActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAzBinding
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var linkAdapter: LinkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAzBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val database = AppDatabase.getDatabase(this)
        val repository = LinkRepository(database.linkDao())
        val factory = LinkViewModelFactory(repository)
        linkViewModel = ViewModelProvider(this, factory).get(LinkViewModel::class.java)

        linkAdapter = LinkAdapter(listOf()) { link ->
            val updatedLink = link.copy(isFavorite = !link.isFavorite)
            linkViewModel.update(updatedLink)
        }
        binding.linksRecyclerView.adapter = linkAdapter

        linkViewModel.allLinks.observe(this) { links ->
            linkAdapter.updateLinks(links)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                linkViewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }
}
