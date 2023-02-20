package com.code4rox.medialoaderx

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.*
import java.io.File


data class ImageItem(
    var uri: Uri,
    val imageId: Long,
    val name: String,
    val title: String,
    val path: String,
    val size: Long,
    val modified: Long,
    val dateAdded: Long,
)


data class ImageFolder(
    val folderId: String,
    val folderName: String,
    var folderPath: String,
    var imageList: ArrayList<ImageItem>,
)

class ImageLoaderX(var mContext: Context) {

    private var mJob: Job = Job()

    var mSelection: String? = null
    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? = null

    var mFilterImageExt: Array<String>? = null

    private val mProjection: Array<String> by lazy {
        arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )
    }

    fun getAllImages(
        onImageListSuccess: ((imageList: ArrayList<ImageItem>) -> Unit)? = null,
        onImageFolderListSuccess: ((imageFolderList: ArrayList<ImageFolder>) -> Unit)? = null,
        onFailed: ((error: String) -> Unit)? = null,
    ) {

        mJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageList = ArrayList<ImageItem>()
                val imageFolderList = ArrayList<ImageFolder>()

                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                val query = mContext.contentResolver.query(collection, mProjection, mSelection, mSelectionArgs, mSortOrder)

                query?.use { cursor ->

                    while (cursor.moveToNext()) {

                        val imageId: Long = cursor.getColumnLong(MediaStore.Images.Media._ID)
                        val name: String = cursor.getColumnString(MediaStore.Images.Media.DISPLAY_NAME)
                        val title: String = cursor.getColumnString(MediaStore.Images.Media.TITLE)
                        val path: String = cursor.getColumnString(MediaStore.Images.Media.DATA)
                        val size: Long = cursor.getColumnLong(MediaStore.Images.Media.SIZE)
                        val modified: Long = cursor.getColumnLong(MediaStore.Images.Media.DATE_MODIFIED)
                        val dateAdded: Long = cursor.getColumnLong(MediaStore.Images.Media.DATE_ADDED)
                        val folderId: String = cursor.getColumnString(MediaStore.Images.Media.BUCKET_ID)
                        var folderName: String = cursor.getColumnString(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                        if (folderName.isEmpty()) folderName = "Internal Storage"

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imageId
                        )

                        val imageItem = ImageItem(
                            uri = contentUri,
                            imageId = imageId,
                            name = name,
                            title = title,
                            path = path,
                            size = size,
                            modified = modified,
                            dateAdded = dateAdded,
                        )


                        fun addDataToLists() {

                            imageList += imageItem

                            if (onImageFolderListSuccess != null) {
                                // File folder list
                                val folder = imageFolderList.firstOrNull { it.folderId == folderId }
                                if (folder != null) {
                                    folder.imageList.add(imageItem)
                                } else {
                                    imageFolderList.add(ImageFolder(folderId,
                                        folderName,
                                        path,
                                        ArrayList<ImageItem>().apply { add(imageItem) }))
                                }
                            }
                        }


                        if (mFilterImageExt != null) {
                            val ext = File(path).extension
                            if (mFilterImageExt!!.contains(ext)) {
                                addDataToLists()
                            }
                        } else {
                            addDataToLists()
                        }

                    }
                }

                withContext(Dispatchers.Main) {
                    onImageListSuccess?.invoke(imageList)
                    onImageFolderListSuccess?.invoke(imageFolderList)
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