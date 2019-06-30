package com.hussein.imageload

import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import android.widget.ImageView
import java.io.*
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.ExecutorService


class ImageLoader(context: Context) {
    var memoryCache = MemoryCache()
    var fileCache: FileCache=FileCache(context)
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    var executorService: ExecutorService=Executors.newFixedThreadPool(5)

    private var errorResourceId:Int?=0
        get() = field
        set(value) { field = value}
    private var placeholderResourceId:Int?=R.drawable.ic_photo
        get() = field
        set(value) { field = value}

    private var newWidth:Int?=0
        get() = field
        set(value) { field = value}

    private var newHeight:Int?=0
        get() = field
        set(value) { field = value}

    private var bitmapIMG:Bitmap?=null
        get() = field
        set(value) { field = value}


    //set Image Error
    fun setError(resourceID:Int):ImageLoader
    {
        errorResourceId=resourceID
        return this
    }
    //set Image Placeholder
    fun placeholder(resourceID:Int):ImageLoader
    {
        placeholderResourceId=resourceID
        return this
    }

    //set size image
    fun resize(width:Int,height:Int):ImageLoader
    {
        newWidth=width
        newHeight=height
        return this
    }

    fun DisplayImage(url: String, imageView: ImageView):ImageLoader {
        imageViews[imageView] = url
        val bitmap = memoryCache[url]
        if(newWidth!=0) {
            imageView.layoutParams.width = newWidth!!
        }
        if(newHeight!=0) {
            imageView.layoutParams.height = newHeight!!
        }
        if (bitmap != null)
            imageView.setImageBitmap(bitmap)
        else {
            queuePhoto(url, imageView)
            imageView.setImageResource(placeholderResourceId!!)
        }
        return this
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        val p = PhotoToLoad(url, imageView)
        executorService.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache.getFile(url)

        //from SD cache
        val b = decodeFile(f)
        if (b != null)
            return b

        //from web
        try {
            val imageUrl = URL(url)
            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            val `is` = conn.inputStream
            val os = FileOutputStream(f)
            Utils.CopyStream(`is`, os)
            os.close()
            bitmapIMG = decodeFile(f)
            return bitmapIMG
        } catch (ex: Throwable) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError)
                memoryCache.clear()
            return null
        }

    }

    //decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File): Bitmap? {
        try {
            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            //Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 70
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }

        return null
    }

    //Task for the queue
    inner class PhotoToLoad(var url: String, var imageView: ImageView)

    internal inner class PhotosLoader(var photoToLoad: PhotoToLoad) : Runnable {

        override fun run() {
            if (imageViewReused(photoToLoad))
                return
            val bmp = getBitmap(photoToLoad.url)
            memoryCache.put(photoToLoad.url, bmp!!)
            if (imageViewReused(photoToLoad))
                return
            val bd = BitmapDisplayer(bmp, photoToLoad)
            val a = photoToLoad.imageView.getContext() as Activity
            a.runOnUiThread(bd)
        }
    }

    fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews.get(photoToLoad.imageView)
        return tag == null || tag != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    internal inner class BitmapDisplayer(var bitmap: Bitmap?, var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad))
                return
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap)
            else
                photoToLoad.imageView.setImageResource(placeholderResourceId!!)
        }
    }

    fun clearCache() {
        memoryCache.clear()
        fileCache.clear()
    }

}