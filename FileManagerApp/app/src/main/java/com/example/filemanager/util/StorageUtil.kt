package com.example.filemanager.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.example.filemanager.model.FileItem
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object StorageUtil {
    /** Returns the root Uri for external storage using SAF. */
    fun getRootUri(context: Context): Uri {
        // For simplicity, use the primary external storage directory
        val external = android.os.Environment.getExternalStorageDirectory()
        return Uri.fromFile(external)
    }

    /** List files in the given directory Uri using DocumentFile. */
    fun listFiles(context: Context, dirUri: Uri): List<FileItem> {
        val docFile = DocumentFile.fromTreeUri(context, dirUri) ?: return emptyList()
        return docFile.listFiles().map { df ->
            FileItem(
                uri = df.uri,
                name = df.name ?: "",
                size = df.length(),
                isDirectory = df.isDirectory,
                lastModified = df.lastModified(),
                mimeType = df.type
            )
        }
    }

    /** Delete a file/folder given its Uri. */
    fun deleteFile(context: Context, fileUri: Uri): Boolean {
        val docFile = DocumentFile.fromSingleUri(context, fileUri) ?: return false
        return docFile.delete()
    }

    /** Copy from src to dest directory. Returns the new Uri or null. */
    fun copyFile(context: Context, srcUri: Uri, destDirUri: Uri): Uri? {
        val src = DocumentFile.fromSingleUri(context, srcUri) ?: return null
        val destDir = DocumentFile.fromTreeUri(context, destDirUri) ?: return null
        if (!src.isFile) return null
        val newFile = destDir.createFile(src.type ?: "application/octet-stream", src.name ?: "copy")
        newFile?.uri?.let { destUri ->
            context.contentResolver.openInputStream(srcUri).use { input ->
                context.contentResolver.openOutputStream(destUri).use { output ->
                    copyStream(input, output)
                }
            }
        }
        return newFile?.uri
    }

    private fun copyStream(input: InputStream?, output: OutputStream?) {
        if (input == null || output == null) return
        val buffer = ByteArray(8192)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
        }
        output.flush()
    }

    /** Zip a list of files (provided as Uris) into a destination Uri (must be a folder). */
    fun zipFiles(context: Context, fileUris: List<Uri>, destFolderUri: Uri, zipName: String = "archive.zip"): Uri? {
        val destDir = DocumentFile.fromTreeUri(context, destFolderUri) ?: return null
        val zipFile = destDir.createFile("application/zip", zipName) ?: return null
        context.contentResolver.openOutputStream(zipFile.uri).use { out ->
            ZipOutputStream(out).use { zos ->
                fileUris.forEach { uri ->
                    val doc = DocumentFile.fromSingleUri(context, uri) ?: return@forEach
                    context.contentResolver.openInputStream(uri).use { input ->
                        val entry = ZipEntry(doc.name ?: "file")
                        entry.size = doc.length()
                        zos.putNextEntry(entry)
                        copyStream(input, zos)
                        zos.closeEntry()
                    }
                }
            }
        }
        return zipFile.uri
    }

    /** Unzip a zip file Uri into the destination folder Uri. */
    fun unzipFile(context: Context, zipUri: Uri, destFolderUri: Uri) {
        val destDir = DocumentFile.fromTreeUri(context, destFolderUri) ?: return
        context.contentResolver.openInputStream(zipUri).use { input ->
            ZipInputStream(input).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val newFile = destDir.createFile("application/octet-stream", entry.name)
                        newFile?.uri?.let { outUri ->
                            context.contentResolver.openOutputStream(outUri).use { out ->
                                copyStream(zis, out)
                            }
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }
}
