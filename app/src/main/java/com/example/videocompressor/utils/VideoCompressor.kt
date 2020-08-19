package com.example.videocompressor.utils

import android.content.Context
import android.util.Log
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import java.io.File


class VideoCompressor(var context: Context?) {

    val SUCCESS = 1
    val FAILED = 2
    val NONE = 3
    val RUNNING = 4
    init {
        FFmpeg.getInstance(context).loadBinary(null)
    }

    private var status = NONE
    private var errorMessage: String? = "Compression Failed!"


    fun startCompressing(
        videoBitrate : String,
        inputPath: String?,
        listener: CompressionListener?
    ) {
        if ( inputPath.isNullOrEmpty() || videoBitrate.isEmpty()) {
            status = NONE
            listener?.compressionFinished(NONE,null)
            return
        }
        var outputPath = ""
        outputPath = getAppDir()
        val commandParams = arrayOf<String>(
            "-y",
            "-i",
            inputPath,
            "-s",
            "1280x720",
            "-r",
            "25",
            "-vcodec",
            "mpeg4",
            "-b:v",
            videoBitrate+"k",
            "-b:a",
            "48000",
            "-ac",
            "2",
            "-ar",
            "22050",
            outputPath
        )
        compressVideo(commandParams, outputPath, listener)
    }

    fun getAppDir(): String {
        var outputPath: String = context?.filesDir!!.absolutePath
        outputPath += "/" + "CompressedVideos"
        val file = File(outputPath)
        if (!file.exists()) {
            file.mkdir()
        }

        outputPath = outputPath + "/compressed_video"+System.currentTimeMillis()+".mp4"
        return outputPath
    }

    private fun compressVideo(
        command: Array<String>,
        outputFilePath: String,
        listener: CompressionListener?
    ) {
        try {
            FFmpeg.getInstance(context).execute(command, object : FFmpegExecuteResponseHandler {
                override fun onSuccess(message: String) {
                    status = SUCCESS
                }

                override fun onProgress(message: String) {
                    status = RUNNING
                    Log.e("---CompressorProgress", message)
                }

                override fun onFailure(message: String) {
                    status = FAILED
                    Log.e("---Compressor", message)
                    listener?.onFailure("Error : $message")
                }

                override fun onStart() {
                    Log.e("---Compressor", "Started")
                }
                override fun onFinish() {
                    Log.e("---Compressor", "finished")
                    listener?.compressionFinished(status, outputFilePath)
                }
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            status = FAILED
            errorMessage = e.message
            listener?.onFailure("Error : " + e.message)
        }
    }

    fun stopCompression() {
        if (FFmpeg.getInstance(context).isFFmpegCommandRunning) {
            FFmpeg.getInstance(context).killRunningProcesses()
        }
    }

    interface CompressionListener {
        fun compressionFinished(
            status: Int, fileOutputPath: String?
        )
        fun onFailure(message: String?)
        fun onProgress(progress: Int)
    }

}