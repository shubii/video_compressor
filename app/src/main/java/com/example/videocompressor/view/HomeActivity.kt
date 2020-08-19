package com.example.videocompressor.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.videocompressor.databinding.ActivityMainBinding
import com.example.videocompressor.viewmodel.HomeViewModel



class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel : HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootLayout)
        binding.btnChoose.setOnClickListener {
            setReadStoragePermission()
        }
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.getVideoPathLivedata().observe(this, Observer {path ->
            path?.let {
                VideoCompressActivity.start(this@HomeActivity, path)
            }
        })
    }

    //Pick a video file from device
    private fun pickVideo() {
        val intent = Intent()
        intent.apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select video"), 1000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000 && data != null && data.data != null) {
                val uri = data.data
                uri?.apply {
                    viewModel.getVideoPath(uri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickVideo()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            return
        }
        pickVideo()
    }

}
