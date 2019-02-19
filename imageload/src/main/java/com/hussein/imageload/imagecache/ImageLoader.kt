package com.hussein.imageloaderlibrary.imageload

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.hussein.imageloaderlibrary.FileCache
import com.hussein.imageloaderlibrary.R
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

class ImageLoader/*private static ImageLoader instance;
    public static ImageLoader getInstance(Context context)
    {
        if (instance == null){ //if there is no instance available... create new one
            instance = new ImageLoader(context);
        }

        return instance;
    }*/
    (context: Context) {
    internal var memoryCache = MemoryCache()
    internal var fileCache: FileCache? = null
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    internal var executorService: ExecutorService? = null

    internal val stub_id = R.mipmap.ic_launcher

    init {
        fileCache = FileCache(context)
        executorService = Executors.newFixedThreadPool(5)
    }

    fun DisplayImage(url: String, imageView: ImageView) {
        imageViews[imageView] = url
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
        executorService!!.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache!!.getFile(url)

        //from SD cache
        val b = decodeFile(f)
        if (b != null)
            return b
        try {
            var bitmap: Bitmap? = null
            val imageUrl = URL(url)
            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            val `is` = conn.inputStream
            val os = FileOutputStream(f)
            Utils.CopyStream(`is`, os)
            os.close()
            bitmap = decodeFile(f)
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

}
