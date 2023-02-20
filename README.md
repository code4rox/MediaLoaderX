MediaLoaderX
====
Use this library , you can load pictures,videos,audios very fast in Phone Storage.

# Dependency

```gradle
repositories {

        maven { url 'https://www.jitpack.io' } 
        
}
```


```gradle
dependencies {

        implementation 'com.github.code4rox:MediaLoaderX:1.0'
        
}
```



add permission

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

## USE
**Load all Images and Images Folders**

<br>

```kotlin
  ImageLoaderX(context).getAllImages({ images ->
  
            //  all images list 
            
        }, { imageFolders ->
        
            // all images folder list
            
        })
```
<br>

**Load all Audios and Audios Folders**

<br>

```kotlin
      AudioLoaderX(context).getAllAudios({ audios ->

            //  all audios list

        }, { audioFolders ->

            // all audios folder list

        })

```



<br>

**Load all Videos and Videos Folders**

<br>

```kotlin
   VideoLoaderX(context).getAllVideos ({ videos ->

            //  all videos list 

        }, { videosFolders ->

            // all videos folder list

        })
```
<br>

**Load all Files and Files Folders**

<br>

```kotlin
   FileLoaderX(context).getAllFiles  ({ files ->

            //  all files list

        }, { fileFolders ->

            // all files folder list

        })

```

