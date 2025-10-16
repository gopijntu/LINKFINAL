package com.clipvault.ui

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var shakeCount: Int = 0

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

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        binding.contentMain.navAzCustom.setOnClickListener {
            startActivity(Intent(this, AZActivity::class.java))
        }

        binding.contentMain.navFavoritesCustom.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Set up category grid
        val categories = listOf(
            Category("Finance", iconRes = R.drawable.ic_finance),
            Category("Tech", iconRes = R.drawable.ic_tech),
            Category("Food", iconRes = R.drawable.ic_food),
            Category("Fitness", iconRes = R.drawable.ic_fitness),
            Category("Pets", iconRes = R.drawable.ic_pets),
            Category("Medicine", iconRes = R.drawable.ic_medicine),
            Category("Love", iconRes = R.drawable.ic_love),
            Category("Viral", iconRes = R.drawable.ic_viral),
            Category("Misc", iconRes = R.drawable.ic_misc)
        )

        val adapter = CategoryAdapter(categories) { category ->
            // No-op for now, since the save button handles the logic
        }
        binding.contentMain.categoryGrid.layoutManager = GridLayoutManager(this, 3)
        binding.contentMain.categoryGrid.adapter = adapter

        binding.contentMain.saveButton.setOnClickListener {
            val link = binding.contentMain.addLinkEditText.text.toString()
            val selectedCategory = (binding.contentMain.categoryGrid.adapter as CategoryAdapter).getSelectedCategory()

            if (selectedCategory == null) {
                Toast.makeText(this, getString(R.string.select_category_prompt), Toast.LENGTH_SHORT).show()
            } else {
                // Perform save operation here (e.g., save to database)
                Toast.makeText(this, getString(R.string.saved_to_category, selectedCategory.name), Toast.LENGTH_SHORT).show()
                binding.contentMain.addLinkEditText.text.clear()
                binding.contentMain.saveButton.visibility = View.INVISIBLE
                (binding.contentMain.categoryGrid.adapter as CategoryAdapter).clearSelection()
            }
        }

        updateBottomNavSelection(binding.contentMain.navHomeCustom.id)
    }

    override fun onResume() {
        super.onResume()
        // When returning to MainActivity, ensure the Home icon is selected
        updateBottomNavSelection(binding.contentMain.navHomeCustom.id)
        pasteLinkFromClipboard()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun pasteLinkFromClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if (text != null && Patterns.WEB_URL.matcher(text).matches()) {
                binding.contentMain.addLinkEditText.setText(text)
                binding.contentMain.saveButton.visibility = View.VISIBLE
            }
        }
    }

    private fun updateBottomNavSelection(selectedItemId: Int) {
        val gold = ContextCompat.getColor(this, R.color.gold)
        val gray = ContextCompat.getColor(this, R.color.gray)

        binding.contentMain.navHomeCustom.setTextColor(if (selectedItemId == binding.contentMain.navHomeCustom.id) gold else gray)
        binding.contentMain.navAzCustom.setTextColor(if (selectedItemId == binding.contentMain.navAzCustom.id) gold else gray)
        binding.contentMain.navFavoritesCustom.setTextColor(if (selectedItemId == binding.contentMain.navFavoritesCustom.id) gold else gray)
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val acceleration = sqrt(x * x + y * y + z * z)
            if (acceleration > 12) { // Shake threshold
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) {
                    shakeCount = 0
                }
                if (currentTime - lastShakeTime > 300) {
                    lastShakeTime = currentTime
                    shakeCount++
                    if (shakeCount >= 2) {
                        shakeCount = 0
                        val link = binding.contentMain.addLinkEditText.text.toString()
                        if (link.isNotEmpty()) {
                            Toast.makeText(this, getString(R.string.saved_to_misc), Toast.LENGTH_SHORT).show()
                            binding.contentMain.addLinkEditText.text.clear()
                            binding.contentMain.saveButton.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }
}
