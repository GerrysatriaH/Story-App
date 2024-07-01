package com.gerrysatria.storyapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.gerrysatria.storyapp.R
import com.gerrysatria.storyapp.databinding.ActivityMainBinding
import com.gerrysatria.storyapp.ui.adapter.ListStoriesAdapter
import com.gerrysatria.storyapp.ui.adapter.LoadingStateAdapter
import com.gerrysatria.storyapp.ui.viewmodel.MainViewModel
import com.gerrysatria.storyapp.ui.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListStoriesAdapter
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkStatus()
        setAdapter()
        toAddStoryActivity()

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.action_logout -> mainViewModel.logout()
            R.id.action_map -> startActivity(Intent(this, MapsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkStatus() {
        mainViewModel.getSession().observe(this@MainActivity){ session ->
            showLoading(true)
            if(session.token.isEmpty()){
                showDialog(getString(R.string.alert_main))
                showLoading(false)
            } else {
                showListStories()
                showLoading(false)
            }
        }
    }

    private fun moveToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setAdapter(){
        adapter = ListStoriesAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun showListStories(){
        mainViewModel.getStories().observe(this@MainActivity){ stories ->
            adapter.submitData(lifecycle, stories)
        }
    }

    private fun toAddStoryActivity(){
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun showDialog(message: String){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(getString(R.string.positive_button)) { _, _ ->
            moveToLogin()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLoading(state: Boolean){
        binding.progressBarMain.isVisible = state
    }
}