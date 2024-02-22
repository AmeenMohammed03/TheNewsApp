package com.example.thenewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.ActivityNewsBinding
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.viewModel.NewsViewModel
import com.example.thenewsapp.viewModel.NewsViewModelProviderFactory
import com.google.android.material.navigation.NavigationView

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

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
                    // Handle Select Country option
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
}
