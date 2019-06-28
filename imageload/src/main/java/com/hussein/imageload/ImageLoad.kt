package com.hussein.imageload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
class ImageLoad{
    var context: Context
    private lateinit var cache: ImageCache
    companion object {
        var placehoder: Int = 0
        var error: Int = 0
        private var width: Int = 0
        private var height: Int = 0
        fun placehoder(drawableId: Int) {
            this.placehoder = drawableId
        }
        fun resize(width: Int,height: Int)
        {
            this.width=width
            this.height=height
        }
        fun error(error: Int){
            this.error= error
        }
    }

    private var executorService: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val uiHandler: Handler = Handler(Looper.getMainLooper())
    constructor(context: Context) {
        this.context = context
        setCache(DoubleCache(context))
    }

    fun setCache(cache: ImageCache) {
        this.cache = cache
    }

    fun displayImage(url: String, imageView: ImageView) {
        if (placehoder != 0) {
            imageView.setImageResource(placehoder)
        } else {
            imageView.setImageResource(R.drawable.ic_photo)
        }
        if(width!=0) {
            imageView.layoutParams.width = width
        }
        if(height!=0) {
            imageView.layoutParams.height = height
        }
        val cached = cache.get(url)
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
                cache.put(url, bitmap)
            }
            else
            {
                if(error!=0)
                {
                    imageView.setImageResource(error)
                    clearCache()
                }
            }
        }
    }
        fun clearCache() {
            this.cache.clear()
        }

        private fun updateImageView(imageView: ImageView, bitmap: Bitmap) {
            uiHandler.post { imageView.setImageBitmap(bitmap)
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
