[![](https://jitpack.io/v/code4rox/MediaLoaderX.svg)](https://jitpack.io/#code4rox/MediaLoaderX)


MediaLoaderX
====

In this library you can get fast

- Images 
- Audios
- Videos
- Files

using ```Media Store API``` 

# Dependency

```gradle
repositories {

        maven { url 'https://www.jitpack.io' } 
        
}
```


```gradle
dependencies {

        implementation 'com.github.code4rox:MediaLoaderX:1.0.0'
        
}
```

add permission and allow runtime 

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

## USE
- Load all Images and Images Folders

<br>

```kotlin
  ImageLoaderX(context).getAllImages({ images ->
  
            //  all images list 
            
        }, { imageFolders ->
        
            // all images folder list
            
        })
```
<br>

- Load all Audios and Audios Folders

<br>

```kotlin
      AudioLoaderX(context).getAllAudios({ audios ->

            //  all audios list

        }, { audioFolders ->

            // all audios folder list

        } , { artists ->
           
            // all artist with songs
            
        } , { albums ->
        
           // all albums with songs
        
        })

```



<br>

- Load all Videos and Videos Folders

<br>

```kotlin
   VideoLoaderX(context).getAllVideos ({ videos ->

            //  all videos list 

        }, { videosFolders ->

            // all videos folder list

        })
```
<br>

- Load all Files and Files Folders

<br>

In android 11 and above you need to give  **MANAGE_EXTERNAL_STORAGE**  permission to get all files othervoise this give only media files.
and don't forget add permission in menifest    ```<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />```

**NOTE: MANAGE_EXTERNAL_STORAGE permission is only granted to specific app types, such as file managers or antivirus apps, and you'll need to justify its use when publishing on the Google Play Store.**


```kotlin
   FileLoaderX(context).getAllFiles  ({ files ->

            //  all files list

        }, { fileFolders ->

            // all files folder list

        })

```
 
## Advance Use

if you want get all images or all folders only use like this 



```kotlin
  ImageLoaderX(context).getAllImages({ images ->
            //  all images list  
       )
```


```kotlin
  ImageLoaderX(context).getAllImages({ imageFolders ->
            // all images folder list
        })
```

- Selection , Sorting and Filter

```kotlin 
  VideoLoaderX(this).apply {

            // Show only videos that are at least 5 minutes in duration.
            mSelection = "${MediaStore.Video.Media.DURATION} >= ?"
            mSelectionArgs = arrayOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString())

            // if you want sort
            mSortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"

            // if you want filter
            mFilterVideoExt = arrayOf("mp4", "mkv")

        }.getAllVideos({
            // all videos list
        }, {
            // all videos folder list
        })

```



## Exception handling

and if you have any kind of exception you can deduct like this 



```kotlin
  ImageLoaderX(context).getAllImages({ images ->
  
            //  all images list 
            
        }, { imageFolders ->
        
            // all images folder list
            
        },{ error ->
        
           // error message show here
        
        })
```
