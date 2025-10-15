package com.clipvault.ui.addlink

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.clipvault.R
import com.clipvault.databinding.ActivityAddLinkBinding
import com.clipvault.db.AppDatabase
import com.clipvault.model.Category
import com.clipvault.model.Link
import com.clipvault.repositories.LinkRepository
import com.clipvault.ui.adapter.CategoryAdapter
import com.clipvault.viewmodels.LinkViewModel
import com.clipvault.viewmodels.LinkViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import kotlin.math.abs

class AddLinkActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityAddLinkBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    private var flipCount = 0
    private var lastZ: Float? = null
    private val flipThreshold = 9.0f
    private var lastFlipTime: Long = 0
    private val flipTimeout = 5000L // 5 seconds
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)
        val repository = LinkRepository(database.linkDao())
        val factory = LinkViewModelFactory(repository)
        linkViewModel = ViewModelProvider(this, factory).get(LinkViewModel::class.java)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Handle incoming share intent or clipboard content
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            handleSharedText(intent)
        } else {
            checkClipboard()
        }

        // Set up back button
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Set up category grid
        val categories = listOf(
            Category("Finance", R.drawable.ic_finance),
            Category("Tech", R.drawable.ic_tech),
            Category("Food", R.drawable.ic_food),
            Category("Pets", R.drawable.ic_pets),
            Category("Medicine", R.drawable.ic_medicine),
            Category("Fitness", R.drawable.ic_fitness),
            Category("Love", R.drawable.ic_love),
            Category("Viral", R.drawable.ic_viral),
            Category("Misc", R.drawable.ic_misc)
        )

        categoryAdapter = CategoryAdapter(categories) { /* No op */ }
        binding.categoryGrid.layoutManager = GridLayoutManager(this, 3)  // 3 columns
        binding.categoryGrid.adapter = categoryAdapter

        // Save button logic
        binding.saveButton.setOnClickListener {
            val selectedCategory = categoryAdapter.getSelectedCategory()?.name ?: "Misc"
            saveLink(selectedCategory)
        }
    }

    private fun handleSharedText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            binding.urlInput.setText(it)
        }
    }

    private fun checkClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val clipboardText = item?.text.toString()
            if (android.util.Patterns.WEB_URL.matcher(clipboardText).matches()) {
                binding.urlInput.setText(clipboardText)
            }
        }
    }

    private fun saveLink(category: String) {
        val url = binding.urlInput.text.toString()
        if (url.isBlank()) {
            Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).get()
                val title = doc.title()
                val thumbnailUrl = doc.select("meta[property=og:image]").attr("content")

                val newLink = Link(title = title, url = url, notes = binding.notesInput.text.toString(), thumbnailUrl = thumbnailUrl, category = category, isFavorite = false)
                linkViewModel.insert(newLink)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLinkActivity, "Link saved to $category", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback for when fetching title/thumbnail fails
                    val newLink = Link(title = url, url = url, notes = binding.notesInput.text.toString(), thumbnailUrl = "", category = category, isFavorite = false)
                    linkViewModel.insert(newLink)
                    Toast.makeText(this@AddLinkActivity, "Link saved (could not fetch title/thumbnail)", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        timeoutRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val z = event.values[2]
            if (lastZ != null) {
                if (abs(z - lastZ!!) > flipThreshold) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastFlipTime > 1000) { // Cooldown of 1 sec between flips
                        handleFlip()
                    }
                }
            }
            lastZ = z
        }
    }

    private fun handleFlip() {
        lastFlipTime = System.currentTimeMillis()
        flipCount++

        if (flipCount == 1) {
            Toast.makeText(this, "Flip again to save to Misc", Toast.LENGTH_SHORT).show()
            timeoutRunnable = Runnable {
                flipCount = 0
                Toast.makeText(this, "Flip to save timed out", Toast.LENGTH_SHORT).show()
            }
            handler.postDelayed(timeoutRunnable!!, flipTimeout)
        } else if (flipCount == 2) {
            timeoutRunnable?.let { handler.removeCallbacks(it) }
            saveLink("Misc")
            flipCount = 0
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
