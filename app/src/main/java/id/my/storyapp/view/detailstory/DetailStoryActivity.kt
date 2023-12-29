package id.my.storyapp.view.detailstory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import id.my.storyapp.data.remote.response.ListStoryItem
import id.my.storyapp.databinding.ActivityDetailStoryBinding
import id.my.storyapp.util.DateFormatter
import id.my.storyapp.util.LocationFormatter
import id.my.storyapp.view.main.MainActivity

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStory()
    }

    private fun setStory() {
        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_DETAIL_STORY, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_DETAIL_STORY)
        }

        binding.tvName.text = story?.name
        binding.tvDescription.text = story?.description
        val lat = story?.lat
        val lon = story?.lon
        binding.tvLocation.text = lat?.let { latitude ->
            lon?.let { longitude ->
                LocationFormatter.getPlaceName(
                    this, latitude,
                    longitude
                )
            }
        }
        binding.tvDateTime.text = DateFormatter.formatDate(story?.createdAt.toString())
        Glide.with(this)
            .load(story?.photoUrl)
            .centerCrop()
            .into(binding.ivStoryPhoto)
        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_DETAIL_STORY = "extra_story"
    }
}