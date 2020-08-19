package com.example.videocompressor.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.videocompressor.utils.getMediaPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var videoLiveData : MutableLiveData<String> = MutableLiveData()

    fun getVideoPathLivedata() : LiveData<String> = videoLiveData


    fun getVideoPath(uri: Uri) {
        viewModelScope.launch {
            val result =  async(Dispatchers.Default) {
                getMediaPath(
                    getApplication(),
                    uri
                )
            }

            val path = result.await()
            videoLiveData.value = path
        }
    }
}