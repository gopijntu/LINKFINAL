package com.clipvault.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.clipvault.R
import com.clipvault.databinding.ActivityMainBinding
import com.clipvault.model.Category
import com.clipvault.ui.adapter.CategoryAdapter
import com.clipvault.ui.addlink.AddLinkActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.contentMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        binding.contentMain.addLinkHeaderButton.setOnClickListener {
            startActivity(Intent(this, AddLinkActivity::class.java))
        }

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

        updateBottomNavSelection(R.id.nav_home_custom)
    }

    override fun onResume() {
        super.onResume()
        // When returning to MainActivity, ensure the Home icon is selected
        updateBottomNavSelection(R.id.nav_home_custom)
    }

    private fun updateBottomNavSelection(selectedItemId: Int) {
        val gold = ContextCompat.getColor(this, R.color.gold)
        val gray = ContextCompat.getColor(this, R.color.gray)

        binding.contentMain.navHomeCustom.setColorFilter(if (selectedItemId == R.id.nav_home_custom) gold else gray)
        binding.contentMain.navAzCustom.setColorFilter(if (selectedItemId == R.id.nav_az_custom) gold else gray)
        binding.contentMain.navFavoritesCustom.setColorFilter(if (selectedItemId == R.id.nav_favorites_custom) gold else gray)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home_drawer -> {
                // Already on the home screen
            }
            R.id.nav_az_drawer -> {
                startActivity(Intent(this, AZActivity::class.java))
            }
            R.id.nav_favorites_drawer -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
