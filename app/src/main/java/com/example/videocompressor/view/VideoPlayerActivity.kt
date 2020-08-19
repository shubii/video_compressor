package com.example.videocompressor.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AlertDialog
import com.example.videocompressor.databinding.ActivityVideoPlayerBinding
import com.example.videocompressor.utils.getFileSize
import java.io.File

class VideoPlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoPlayerBinding
    private var uri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val videoView = binding.videoView

        intent?.extras?.let {
            uri = it.getString("uri", "")
        }

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)
        videoView.setVideoPath(uri)

        videoView.setOnPreparedListener {
            it.start()
        }

        val s = "Output video path : " + uri + "\n\n" +
                "Output video size : " + getFileSize(File(uri).length())
        AlertDialog.Builder(this).setMessage(s).show()
    }

    companion object {
        fun start(activity: Activity, uri: String) {
            val intent = Intent(activity, VideoPlayerActivity::class.java).apply {
                putExtra("uri", uri)
            }
            activity.startActivity(intent)
        }

    }
}
