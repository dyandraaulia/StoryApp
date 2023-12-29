package id.my.storyapp.view.poststory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import id.my.storyapp.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setImage()
    }

    private fun setImage() {
        val imgUri = intent.getStringExtra(CameraActivity.EXTRA_URI_IMAGE)
        imgUri?.toUri().let {
            Log.d("Image URI", "showImage: $it")
            binding.ivStoryPhoto.setImageURI(it)
        }
        setupButton(imgUri)
    }

    private fun setupButton(uri: String?) {
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, AddDescriptionActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_URI, uri)
            startActivity(intent)
        }
        binding.btnRetake.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}