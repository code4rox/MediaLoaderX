package com.code4rox.medialoaderx

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.*
import java.io.File


data class AudioItem(
    var uri: Uri,
    var audioId: Long,
    var name: String,
    var title: String,
    var path: String,
    var duration: Long,
    var size: Long,
    var modified: Long,
    var dateAdded: Long,
    var artistId: Long,
    var artist: String,
    var albumId: Long,
    var album: String,
    var folderId: String,
    var folderName: String,
)


data class AudioFolder(
    val folderId: String,
    val folderName: String,
    var folderPath: String,
    var audioList: ArrayList<AudioItem>,
)

data class ArtistsItem(
    val artistId: Long,
    val artistName: String,
    var artistPath: String,
    var artistList: ArrayList<AudioItem>,
)

data class AlbumItem(
    val albumId: Long,
    val albumName: String,
    var albumPath: String,
    var albumList: ArrayList<AudioItem>,
)


data class AudioLoaderX(private var mContext: Context) {

    private var mJob: Job = Job()

    var mSelection: String? = null
    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? = null

    var mFilterAudioExt: Array<String>? = null


    private val mProjection: Array<String> by lazy {
        arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.BUCKET_ID,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
        )
    }

    fun getAllAudios(
        onAudioListSuccess: ((audioList: ArrayList<AudioItem>) -> Unit)? = null,
        onAudioFolderListSuccess: ((audioFolderList: ArrayList<AudioFolder>) -> Unit)? = null,
        onArtistListSuccess: ((artistList: ArrayList<ArtistsItem>) -> Unit)? = null,
        onAlbumListSuccess: ((artistList: ArrayList<AlbumItem>) -> Unit)? = null,
        onFailed: ((error: String) -> Unit)? = null,
    ) {

        mJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val audioList = ArrayList<AudioItem>()
                val audioFolderList = ArrayList<AudioFolder>()
                val artistList = ArrayList<ArtistsItem>()
                val albumList = ArrayList<AlbumItem>()

                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Audio.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                val query = mContext.contentResolver.query(collection, mProjection, mSelection, mSelectionArgs, mSortOrder)

                query?.use { cursor ->

                    while (cursor.moveToNext()) {

                        val audioId: Long = cursor.getColumnLong(MediaStore.Audio.Media._ID)
                        val name: String = cursor.getColumnString(MediaStore.Audio.Media.DISPLAY_NAME)
                        val title: String = cursor.getColumnString(MediaStore.Audio.Media.TITLE)
                        val path: String = cursor.getColumnString(MediaStore.Audio.Media.DATA)
                        val duration: Long = cursor.getColumnLong(MediaStore.Audio.Media.DURATION)
                        val size: Long = cursor.getColumnLong(MediaStore.Audio.Media.SIZE)
                        val modified: Long = cursor.getColumnLong(MediaStore.Audio.Media.DATE_MODIFIED)
                        val dateAdded: Long = cursor.getColumnLong(MediaStore.Audio.Media.DATE_ADDED)
                        val artistId: Long = cursor.getColumnLong(MediaStore.Audio.Media.ARTIST_ID)
                        val artist: String = cursor.getColumnString(MediaStore.Audio.Media.ARTIST)
                        val albumId: Long = cursor.getColumnLong(MediaStore.Audio.Media.ALBUM_ID)
                        val album: String = cursor.getColumnString(MediaStore.Audio.Media.ALBUM)
                        val folderId: String = cursor.getColumnString(MediaStore.Audio.Media.BUCKET_ID)
                        var folderName: String = cursor.getColumnString(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
                        if (folderName.isEmpty()) folderName = "Internal Storage"


                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            audioId
                        )

                        val audioItem = AudioItem(
                            uri = contentUri,
                            audioId = audioId,
                            name = name,
                            title = title,
                            path = path,
                            duration = duration,
                            size = size,
                            modified = modified,
                            dateAdded = dateAdded,
                            artistId = artistId,
                            artist = artist,
                            albumId = albumId,
                            album = album,
                            folderId = folderId,
                            folderName = folderName
                        )


                        fun addDataToLists() {

                            audioList += audioItem

                            if (onAudioFolderListSuccess != null) {
                                // audio folder list
                                val folder = audioFolderList.firstOrNull { it.folderId == folderId }
                                if (folder != null) {
                                    folder.audioList.add(audioItem)
                                } else {
                                    audioFolderList.add(AudioFolder(folderId,
                                        folderName,
                                        path,
                                        ArrayList<AudioItem>().apply { add(audioItem) }))
                                }
                            }



                            if (onArtistListSuccess != null) {
                                // audio artist list
                                val artistItem = artistList.firstOrNull { it.artistId == artistId }
                                if (artistItem != null) {
                                    artistItem.artistList.add(audioItem)
                                } else {
                                    artistList.add(ArtistsItem(artistId,
                                        artist,
                                        path,
                                        ArrayList<AudioItem>().apply { add(audioItem) }))
                                }
                            }


                            if (onAlbumListSuccess != null) {
                                // audio album list
                                val albumItem = albumList.firstOrNull { it.albumId == albumId }
                                if (albumItem != null) {
                                    albumItem.albumList.add(audioItem)
                                } else {
                                    albumList.add(AlbumItem(albumId,
                                        artist,
                                        path,
                                        ArrayList<AudioItem>().apply { add(audioItem) }))
                                }
                            }


                        }


                        if (mFilterAudioExt != null) {
                            val ext = File(path).extension
                            if (mFilterAudioExt!!.contains(ext)) {
                                addDataToLists()
                            }

                        } else {
                            addDataToLists()
                        }

                    }
                }

                withContext(Dispatchers.Main) {
                    onAudioListSuccess?.invoke(audioList)
                    onAudioFolderListSuccess?.invoke(audioFolderList)
                    onArtistListSuccess?.invoke(artistList)
                    onAlbumListSuccess?.invoke(albumList)
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