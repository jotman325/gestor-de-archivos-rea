package com.example.filemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.filemanager.model.FileItem
import com.example.filemanager.util.StorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileViewModel(application: Application) : AndroidViewModel(application) {
    private val _fileList = MutableLiveData<List<FileItem>>(emptyList())
    val fileList: LiveData<List<FileItem>> = _fileList

    private var currentUri = StorageUtil.getRootUri(application)

    fun loadRoot() {
        viewModelScope.launch {
            val files = withContext(Dispatchers.IO) { StorageUtil.listFiles(getApplication(), currentUri) }
            _fileList.value = files
        }
    }

    fun loadDirectory(uri: android.net.Uri) {
        viewModelScope.launch {
            val files = withContext(Dispatchers.IO) { StorageUtil.listFiles(getApplication(), uri) }
            currentUri = uri
            _fileList.value = files
        }
    }

    suspend fun deleteFile(item: FileItem) {
        withContext(Dispatchers.IO) {
            StorageUtil.deleteFile(getApplication(), item.uri)
            // Refresh list after deletion
            val files = StorageUtil.listFiles(getApplication(), currentUri)
            _fileList.postValue(files)
        }
    }

    // TODO: add copy, move, rename, create folder, zip/unzip methods as needed
}
