package id.my.storyapp.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.storyapp.view.poststory.CameraActivity
import id.my.storyapp.R
import id.my.storyapp.data.remote.response.ListStoryItem
import id.my.storyapp.databinding.ActivityMainBinding
import id.my.storyapp.view.ViewModelFactory
import id.my.storyapp.view.signin.SigninActivity
import android.Manifest
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import id.my.storyapp.adapter.ListStoryAdapter
import id.my.storyapp.adapter.LoadingStateAdapter
import id.my.storyapp.databinding.ItemStoryBinding
import id.my.storyapp.view.detailstory.DetailStoryActivity
import id.my.storyapp.view.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = obtainViewModel(this)

        init()
        setupView()
        setUsersData()
        setMenu()
        fabAddPostListener()
    }

    private fun init() {
        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            }
            Log.d(MainActivity::class.java.simpleName, "login as ${user.email}, ${user.token}")
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setUsersData() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvListStory.layoutManager = layoutManager

        val adapter = ListStoryAdapter()
        showLoading(true)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        mainViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
            layoutManager.scrollToPosition(0)

            showLoading(false)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {

            override fun onShareClicked(data: ListStoryItem?) {
                val intent = Intent().apply {
                    val text = getString(
                        R.string.share_message,
                        data?.description?.split(" ")?.take(20)?.joinToString(" "),
                        data?.name
                    )
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                startActivity(intent)
            }

            override fun onItemClicked(data: ListStoryItem?, binding: ItemStoryBinding) {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(binding.ivStoryPhoto, IMAGE_STORY),
                        Pair(binding.tvName, NAME),
                        Pair(binding.tvDescription, DESCRIPTION)
                    )

                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_DETAIL_STORY, data)
                startActivity(intent, optionsCompat.toBundle())
            }
        })
    }

    private fun setMenu() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    mainViewModel.logout()
                    Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.language_settings -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.temp_maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_request_granted), Toast.LENGTH_LONG
                ).show()
                fabAddPostListener()
            } else {
                Toast.makeText(this, getString(R.string.rejected_permission), Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun fabAddPostListener() {
        binding.fabAddPost.setOnClickListener {
            if (!allPermissionsGranted()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
            if (allPermissionsGranted()) {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val IMAGE_STORY = "ImgStory"
        private const val NAME = "Name"
        private const val DESCRIPTION = "Description"
    }
}