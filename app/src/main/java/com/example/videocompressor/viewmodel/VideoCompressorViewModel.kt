package com.example.videocompressor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videocompressor.utils.VideoCompressor

class VideoCompressorViewModel(application: Application) : AndroidViewModel(application) {

    var mVideoCompressor : VideoCompressor
    var result : MutableLiveData<Result> = MutableLiveData()
    var loader : MutableLiveData<Boolean> = MutableLiveData()

    init {
        mVideoCompressor = VideoCompressor(application)
    }

    fun getOutputFile() : LiveData<Result> {
        return result
    }

    fun getLoaderLivedata() : LiveData<Boolean> {
        return loader
    }

    fun compressInputFile(inputPath : String, bitrate : String) {
        loader.value = true
        mVideoCompressor.startCompressing(bitrate, inputPath, object : VideoCompressor.CompressionListener {
            override fun compressionFinished(status: Int, fileOutputPath: String?) {
                loader.value = false
                if (status == mVideoCompressor.SUCCESS) {
                    fileOutputPath?.let {
                        result.value = Result(status, "", outputPath = fileOutputPath)
                    }
                } else if (status == mVideoCompressor.NONE) {
                    result.value = Result(status, "Validate your inputs", null)

                } else if (status == mVideoCompressor.FAILED) {
                    result.value = Result(status, "Something went wrong", null)
                }
            }

            override fun onFailure(message: String?) {
                loader.value = false
                result.value = Result(mVideoCompressor.FAILED, message!!, null)

            }

            override fun onProgress(progress: Int) {
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        mVideoCompressor.stopCompression()
    }

    data class Result(var status : Int, var message : String, var outputPath: String?)

}