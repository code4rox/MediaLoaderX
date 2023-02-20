package com.code4rox.medialoaderx

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.*
import java.io.File


data class FileItem(
    var uri: Uri,
    val fileId: Long,
    val name: String,
    val title: String,
    val path: String,
    val size: Long,
    val modified: Long,
    val dateAdded: Long,
)


data class FileFolder(
    val folderId: String,
    val folderName: String,
    var folderPath: String,
    var fileList: ArrayList<FileItem>,
)

class FileLoaderX(var mContext: Context) {

    private var mJob: Job = Job()

    var mSelection: String? = null
    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? = null

    var mFilterFileExt: Array<String>? = null

    private val mProjection: Array<String> by lazy {
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        )
    }

    fun getAllFiles(
        onFileListSuccess: ((fileList: ArrayList<FileItem>) -> Unit)? = null,
        onFileFolderListSuccess: ((fileFolderList: ArrayList<FileFolder>) -> Unit)? = null,
        onFailed: ((error: String) -> Unit)? = null,
    ) {

        mJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileList = ArrayList<FileItem>()
                val fileFolderList = ArrayList<FileFolder>()

                val collection = MediaStore.Files.getContentUri("external")

                val query = mContext.contentResolver.query(collection, mProjection, mSelection, mSelectionArgs, mSortOrder)

                query?.use { cursor ->

                    while (cursor.moveToNext()) {

                        val fileId: Long = cursor.getColumnLong(MediaStore.Files.FileColumns._ID)
                        val name: String = cursor.getColumnString(MediaStore.Files.FileColumns.DISPLAY_NAME)
                        val title: String = cursor.getColumnString(MediaStore.Files.FileColumns.TITLE)
                        val path: String = cursor.getColumnString(MediaStore.Files.FileColumns.DATA)
                        val size: Long = cursor.getColumnLong(MediaStore.Files.FileColumns.SIZE)
                        val modified: Long = cursor.getColumnLong(MediaStore.Files.FileColumns.DATE_MODIFIED)
                        val dateAdded: Long = cursor.getColumnLong(MediaStore.Files.FileColumns.DATE_ADDED)
                        val folderId: String = cursor.getColumnString(MediaStore.Files.FileColumns.BUCKET_ID)
                        var folderName: String = cursor.getColumnString(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                        if (folderName.isEmpty()) folderName = "Internal Storage"


                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            fileId
                        )

                        val fileItem = FileItem(
                            uri = contentUri,
                            fileId = fileId,
                            name = name,
                            title = title,
                            path = path,
                            size = size,
                            modified = modified,
                            dateAdded = dateAdded,
                        )


                        fun addDataToLists() {

                            fileList += fileItem

                            if (onFileFolderListSuccess != null) {
                                // File folder list
                                val folder = fileFolderList.firstOrNull { it.folderId == folderId }
                                if (folder != null) {
                                    folder.fileList.add(fileItem)
                                } else {
                                    fileFolderList.add(FileFolder(folderId,
                                        folderName,
                                        path,
                                        ArrayList<FileItem>().apply { add(fileItem) }))
                                }
                            }
                        }


                        if (mFilterFileExt != null) {
                            val ext = File(path).extension
                            if (mFilterFileExt!!.contains(ext)) {
                                addDataToLists()
                            }

                        } else {
                            addDataToLists()
                        }

                    }
                }

                withContext(Dispatchers.Main) {
                    onFileListSuccess?.invoke(fileList)
                    onFileFolderListSuccess?.invoke(fileFolderList)
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailed?.invoke(e.message ?: "")
                }
            }
        }

    }


    fun onDestroyLoader() {
        mJob.cancel()
    }


}