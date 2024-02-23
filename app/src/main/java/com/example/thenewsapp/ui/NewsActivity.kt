package com.example.thenewsapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.ActivityNewsBinding
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.viewModel.NewsViewModel
import com.example.thenewsapp.viewModel.NewsViewModelProviderFactory
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Locale

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding
    private lateinit var navigationView: NavigationView
    private lateinit var lastUpdatedTextView: TextView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerlayout

        setSupportActionBar(binding.toolbar)
        lastUpdatedTextView = findViewById(R.id.last_updated_time)


        val newsRepository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        newsViewModel =
            ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Set up the navigation drawer
        navigationView = findViewById(R.id.navigationView)
        setUpDrawer()

        binding.toolbar.setOnClickListener {
            // Open or close the navigation drawer when toolbar is clicked
            if (binding.drawerlayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerlayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerlayout.openDrawer(GravityCompat.START)
            }
        }

        val latestNewsMenuItem = binding.bottomNavigationView.menu.findItem(R.id.headlinesFragment2)
        latestNewsMenuItem.setOnMenuItemClickListener {
            // Navigate to headlines fragment when Latest News icon is clicked
            navController.navigate(R.id.headlinesFragment2)
            true // Return true to indicate that the click event is consumed
        }

        val searchMenuItem = binding.bottomNavigationView.menu.findItem(R.id.searchFragment2)
        searchMenuItem.setOnMenuItemClickListener {
            // Navigate to search fragment when search icon is clicked
            navController.navigate(R.id.searchFragment2)
            true // Return true to indicate that the click event is consumed
        }
    }

    private fun setUpDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerlayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_select_country -> {
                    showCountrySearchDialog(drawerLayout)
                    true
                }
                R.id.menu_exit -> {
                    // Handle Exit option
                    finish() // Close the activity
                    true
                }
                else -> false
            }
        }
    }

    private fun showCountrySearchDialog(drawerLayout: DrawerLayout) {
        val countryCodes = newsViewModel.getCountryCodes()

        val dialogView = layoutInflater.inflate(R.layout.country_search_dialog, null)
        val editTextCountrySearch = dialogView.findViewById<EditText>(R.id.editTextCountrySearch)
        val listViewCountryCodes = dialogView.findViewById<ListView>(R.id.listViewCountrySearch) // Correct id

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countryCodes)
        listViewCountryCodes.adapter = adapter

        editTextCountrySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s)
            }
        })

        // Set up dialog buttons
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        // Handle item click in the list view
        listViewCountryCodes.setOnItemClickListener { parent, view, position, id ->
            val selectedCountryCode = parent.getItemAtPosition(position) as String
            // Pass the selected country code to the API
            if (selectedCountryCode == "us") {
                // Fetch news for the default country (us)
                newsViewModel.getHeadlines("us")
            } else {
                // Fetch news for the selected country code
                newsViewModel.getHeadlines(selectedCountryCode)
            }
            dialog.dismiss()

            // Close the navigation drawer
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        dialog.show()
    }


    fun updateLastUpdatedTime() {
        val currentTime = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(currentTime)
        // Assuming you have a TextView with id last_updated_time
        binding.toolbar.findViewById<TextView>(R.id.last_updated_time).apply {
            text = getString(R.string.last_updated, formattedTime)
            visibility = View.VISIBLE
        }
    }

}
