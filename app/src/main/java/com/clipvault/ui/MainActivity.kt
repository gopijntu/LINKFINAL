package com.clipvault.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.clipvault.R
import com.clipvault.databinding.ActivityMainBinding
import com.clipvault.model.Category
import com.clipvault.ui.adapter.CategoryAdapter
import com.clipvault.ui.addlink.AddLinkActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.contentMain.toolbar)

        binding.contentMain.navAzCustom.setOnClickListener {
            startActivity(Intent(this, AZActivity::class.java))
        }

        binding.contentMain.navFavoritesCustom.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Set up category grid
        val categories = listOf(
            Category("Finance", R.drawable.ic_finance),
            Category("Tech", R.drawable.ic_tech),
            Category("Food", R.drawable.ic_food),
            Category("Fitness", R.drawable.ic_fitness),
            Category("Pets", R.drawable.ic_pets),
            Category("Medicine", R.drawable.ic_medicine),
            Category("Love", R.drawable.ic_love),
            Category("Viral", R.drawable.ic_viral),
            Category("Misc", R.drawable.ic_misc)
        )

        val adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, CategoryLinksActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category.name)
            startActivity(intent)
        }
        binding.contentMain.categoryGrid.layoutManager = GridLayoutManager(this, 3)
        binding.contentMain.categoryGrid.adapter = adapter
    }
}
