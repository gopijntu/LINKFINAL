package com.clipvault.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clipvault.databinding.ActivityFavoritesBinding
import com.clipvault.db.AppDatabase
import com.clipvault.model.Link
import com.clipvault.repositories.LinkRepository
import com.clipvault.ui.adapter.LinkAdapter
import com.clipvault.viewmodels.LinkViewModel
import com.clipvault.viewmodels.LinkViewModelFactory

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var linkViewModel: LinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val database = AppDatabase.getDatabase(this)
        val repository = LinkRepository(database.linkDao())
        val factory = LinkViewModelFactory(repository)
        linkViewModel = ViewModelProvider(this, factory).get(LinkViewModel::class.java)

        val adapter = LinkAdapter(listOf()) { link ->
            val updatedLink = link.copy(isFavorite = !link.isFavorite)
            linkViewModel.update(updatedLink)
        }
        binding.linksRecyclerView.adapter = adapter

        linkViewModel.favoriteLinks.observe(this) { links ->
            adapter.updateLinks(links)
        }
    }
}
