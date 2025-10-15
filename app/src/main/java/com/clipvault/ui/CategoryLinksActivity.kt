package com.clipvault.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clipvault.databinding.ActivityCategoryLinksBinding
import com.clipvault.db.AppDatabase
import com.clipvault.repositories.LinkRepository
import com.clipvault.ui.adapter.LinkAdapter
import com.clipvault.viewmodels.LinkViewModel
import com.clipvault.viewmodels.LinkViewModelFactory

class CategoryLinksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryLinksBinding
    private lateinit var linkViewModel: LinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryLinksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("CATEGORY_NAME") ?: "Unknown"
        binding.toolbar.title = category
        binding.toolbar.setNavigationOnClickListener { finish() }

        val database = AppDatabase.getDatabase(this)
        val repository = LinkRepository(database.linkDao())
        val factory = LinkViewModelFactory(repository)
        linkViewModel = ViewModelProvider(this, factory).get(LinkViewModel::class.java)

        val adapter = LinkAdapter(listOf()) // Empty list initially
        binding.linksRecyclerView.adapter = adapter

        linkViewModel.getLinksByCategory(category).observe(this) { links ->
            adapter.updateLinks(links)
        }
    }
}
