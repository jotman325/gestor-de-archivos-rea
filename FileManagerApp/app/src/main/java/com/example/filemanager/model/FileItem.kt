package com.example.filemanager.model

data class FileItem(
    val uri: android.net.Uri,
    val name: String,
    val size: Long,
    val isDirectory: Boolean,
    val lastModified: Long,
    val mimeType: String?
)