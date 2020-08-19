package com.example.videocompressor.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.videocompressor.databinding.ActivityVideoCompressorBinding
import com.example.videocompressor.utils.VideoCompressor
import com.example.videocompressor.utils.VideoCompressor.CompressionListener
import com.example.videocompressor.utils.getFileSize
import com.example.videocompressor.viewmodel.VideoCompressorViewModel
import kotlinx.android.synthetic.main.activity_video_compressor.*
import java.io.File


class VideoCompressActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoCompressorBinding
    private var mInputPath = ""
    lateinit var viewModel : VideoCompressorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(VideoCompressorViewModel::class.java)
        binding = ActivityVideoCompressorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        intent?.extras?.let { mInputPath = it.getString("uri", "") }

        setUpVideo(mInputPath)

        btn_compress.setOnClickListener {
            if (!TextUtils.isEmpty(edt_bitrate.text.toString().trim())) {
                startMediaCompression(edt_bitrate.text.toString().trim())
            } else {
                Toast.makeText(applicationContext, "Please enter bitrate to compress", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getOutputFile().observe(this, Observer { result ->
            if (result.outputPath.isNullOrEmpty().not()) {
                VideoPlayerActivity.start(this@VideoCompressActivity, result.outputPath!!)
            } else {
                var errorMsg = "Something went wrong"
                result.outputPath?.let {
                    errorMsg = it
                }
                AlertDialog.Builder(this@VideoCompressActivity).setMessage(errorMsg)
                    .show()
            }
        })

        viewModel.getLoaderLivedata().observe(this, Observer { doShow ->
            showhideLoader(doShow)
        })
    }

    private fun setUpVideo(mInputPath: String) {
        val videoView = binding.videoView
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoPath(mInputPath)
        videoView.setOnPreparedListener {
            it.start()
        }

        val s = "Input video path : " + mInputPath + "\n" +
                "Input video size : " + getFileSize(File(mInputPath).length()) + "mb"
        Log.d("-----Video Compressor", s)
    }

    private fun startMediaCompression(bitrate:String) {
        viewModel.compressInputFile(inputPath = mInputPath, bitrate = bitrate)
    }

    companion object {
        fun start(activity: Activity, uri: String) {
            val intent = Intent(activity, VideoCompressActivity::class.java).apply {
                putExtra("uri", uri)
            }
            activity.startActivity(intent)
        }
    }

    fun showhideLoader(show : Boolean) {
        if (show) {
            progress_bar.setVisibility(View.VISIBLE)
            btn_compress.visibility = View.GONE
        } else {
            progress_bar.setVisibility(View.GONE)
            btn_compress.visibility = View.VISIBLE
        }
    }
}