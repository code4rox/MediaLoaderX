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

## Default Loader
**Load Images**

load images folders
<br>

```kotlin
  ImageLoaderX(context).getAllImages({ images ->
  
            //  all images list 
            
        }, { imageFolders ->
        
            // all images folder list
            
        })
```
<br>
load all images
<br>

```kotlin
   VideoLoaderX(context).getAllVideos ({ videos ->

            //  all videos list 

        }, { videosFolders ->

            // all videos folder list

        })
```
**Load Videos**
