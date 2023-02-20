package com.code4rox.medialoaderx

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.*
import java.io.File


data class VideoItem(
    var uri: Uri,
    var videoId: Long,
    var name: String,
    var title: String,
    var path: String,
    var duration: Long,
    var size: Long,
    var modified: Long,
    var dateAdded: Long,
    var folderId: String,
    var folderName: String,
)


data class VideoFolder(
    val folderId: String,
    val folderName: String,
    var folderPath: String,
    var videoList: ArrayList<VideoItem>,
)

class VideoLoaderX(var mContext: Context) {

    private var mJob: Job = Job()

    var mSelection: String? = null
    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? = null

    var mFilterVideoExt: Array<String>? = null

    private val mProjection: Array<String> by lazy {
        arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        )
    }

    fun getAllVideos(
        onVideoListSuccess: ((videoList: ArrayList<VideoItem>) -> Unit)? = null,
        onVideoFolderListSuccess: ((videoFolderList: ArrayList<VideoFolder>) -> Unit)? = null,
        onFailed: ((error: String) -> Unit)? = null,
    ) {

        mJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val videoList = ArrayList<VideoItem>()
                val videoFolderList = ArrayList<VideoFolder>()

                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Video.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }

                val query = mContext.contentResolver.query(collection, mProjection, mSelection, mSelectionArgs, mSortOrder)

                query?.use { cursor ->

                    while (cursor.moveToNext()) {

                        val videoId: Long = cursor.getColumnLong(MediaStore.Video.Media._ID)
                        val name: String = cursor.getColumnString(MediaStore.Video.Media.DISPLAY_NAME)
                        val title: String = cursor.getColumnString(MediaStore.Video.Media.TITLE)
                        val path: String = cursor.getColumnString(MediaStore.Video.Media.DATA)
                        val duration: Long = cursor.getColumnLong(MediaStore.Video.Media.DURATION)
                        val size: Long = cursor.getColumnLong(MediaStore.Video.Media.SIZE)
                        val modified: Long = cursor.getColumnLong(MediaStore.Video.Media.DATE_MODIFIED)
                        val dateAdded: Long = cursor.getColumnLong(MediaStore.Video.Media.DATE_ADDED)
                        val folderId: String = cursor.getColumnString(MediaStore.Video.Media.BUCKET_ID)
                        var folderName: String = cursor.getColumnString(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                        if (folderName.isEmpty()) folderName = "Internal Storage"

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            videoId
                        )

                        val videoItem = VideoItem(
                            uri = contentUri,
                            videoId = videoId,
                            name = name,
                            title = title,
                            path = path,
                            duration = duration,
                            size = size,
                            modified = modified,
                            dateAdded = dateAdded,
                            folderId = folderId,
                            folderName = folderName
                        )


                        fun addDataToLists() {

                            videoList += videoItem

                            if (onVideoFolderListSuccess != null) {
                                // video folder list
                                val folder = videoFolderList.firstOrNull { it.folderId == folderId }
                                if (folder != null) {
                                    folder.videoList.add(videoItem)
                                } else {

                                    videoFolderList.add(VideoFolder(folderId,
                                        folderName,
                                        path,
                                        ArrayList<VideoItem>().apply { add(videoItem) }))
                                }
                            }
                        }


                        if (mFilterVideoExt != null) {
                            val ext = File(path).extension
                            if (mFilterVideoExt!!.contains(ext)) {
                                addDataToLists()
                            }

                        } else {
                            addDataToLists()
                        }

                    }
                }

                withContext(Dispatchers.Main) {
                    onVideoListSuccess?.invoke(videoList)
                    onVideoFolderListSuccess?.invoke(videoFolderList)
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