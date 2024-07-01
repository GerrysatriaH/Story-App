package com.gerrysatria.storyapp.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gerrysatria.storyapp.R
import com.gerrysatria.storyapp.data.database.StoryEntity
import com.gerrysatria.storyapp.databinding.ActivityDetailStoryBinding
import com.gerrysatria.storyapp.utils.loadImage

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.detail_story)
            setDisplayHomeAsUpEnabled(true)
        }

        showData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun showData(){
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra(EXTRA_DATA, StoryEntity::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_DATA)
        }

        binding.apply {
            if (data != null) {
                ivDetailPhoto.loadImage(data.photoUrl)
                tvDetailName.text = data.name
                tvDetailName.contentDescription = data.name
                tvDetailDescription.text = data.description
                tvDetailDescription.contentDescription = data.description
            }
        }
    }

    companion object{
        const val EXTRA_DATA = "extra_data"
    }
}