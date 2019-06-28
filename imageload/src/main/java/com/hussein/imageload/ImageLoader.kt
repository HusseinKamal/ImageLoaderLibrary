package com.hussein.imageload

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("NAME_SHADOWING")
class ImageLoader(context: Context) {
    internal var memoryCache = MemoryCache()
    internal var fileCache: FileCache? = null
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    private var executorService: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    internal val stub_id = R.drawable.ic_photo

    init {
        fileCache = FileCache(context)
        executorService = Executors.newFixedThreadPool(5)
    }

    fun DisplayImage(url: String, imageView: ImageView) {
        imageViews.put(imageView,url)
        val bitmap = memoryCache.get(url)
        if (bitmap != null)
            imageView.setImageBitmap(bitmap)
        else {
            queuePhoto(url, imageView)
            imageView.setImageResource(stub_id)
        }
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        val p = PhotoToLoad(url, imageView)
        executorService.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache!!.getFile(url)

        //from SD cache
        val b = decodeFile(f)
        if (b != null)
            return b
        try {
            val bitmap: Bitmap = decodeFile(f)!!
            val imageUrl = URL(url)
            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            val `is` = conn.inputStream
            val os = FileOutputStream(f)
            Utils.CopyStream(`is`, os)
            os.close()
            return bitmap
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
            val REQUIRED_SIZE = 200
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            //resize image or not*****************************************************
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }
            //resize image or not*****************************************************
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
    internal inner class PhotosLoader(var photoToLoad: PhotoToLoad?) : Runnable {
        override fun run() {
            if (imageViewReused(this.photoToLoad!!))
                return
            val bmp = getBitmap(photoToLoad!!.url)
            memoryCache.put(photoToLoad!!.url, bmp!!)
            if (imageViewReused(photoToLoad!!))
                return
            val bd = BitmapDisplayer(bmp, photoToLoad)
            val a = photoToLoad!!.imageView.context as Activity
            a.runOnUiThread(bd)
        }
    }

    internal fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        return tag == null || tag != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    internal inner class BitmapDisplayer(var bitmap: Bitmap?, var photoToLoad: PhotoToLoad?) : Runnable {
        override fun run() {
            if (imageViewReused(this.photoToLoad!!))
                return
            if (bitmap != null)
                photoToLoad!!.imageView.setImageBitmap(bitmap)
            else
                photoToLoad!!.imageView.setImageResource(stub_id)
        }
    }

    fun clearCache() {
        memoryCache.clear()
        fileCache!!.clear()
    }
   private var imageCache: LruCache<String, Bitmap>
    private val uiHandler: Handler = Handler(Looper.getMainLooper())

    init {
        val maxMemory: Long = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize: Int = (maxMemory / 4).toInt()

        imageCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                return (bitmap?.rowBytes ?: 0) * (bitmap?.height ?: 0) / 1024
            }
        }
    }

    fun displayImage(url: String, imageView: ImageView) {
        val cached = imageCache.get(url)
        if (cached != null) {
            updateImageView(imageView, cached)
            return
        }

        imageView.tag = url
        executorService.submit {
            val bitmap: Bitmap? = downloadImage(url)
            if (bitmap != null) {
                if (imageView.tag == url) {
                    updateImageView(imageView, bitmap)
                }
                imageCache.put(url, bitmap)
            }
        }
    }

    private fun updateImageView(imageView: ImageView, bitmap: Bitmap) {
        uiHandler.post {
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun downloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val url = URL(url)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            bitmap = BitmapFactory.decodeStream(conn.inputStream)
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }
}