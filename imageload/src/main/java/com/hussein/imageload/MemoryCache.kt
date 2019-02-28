package com.hussein.imageload

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache

import java.util.Collections
import java.util.LinkedHashMap

class MemoryCache : ImageCache {
    private val cache: LruCache<String, Bitmap>
    init {
        val maxMemory: Long = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize: Int = (maxMemory / 4).toInt()

        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                return (bitmap?.rowBytes ?: 0) * (bitmap?.height ?: 0) / 1024
            }
        }
    }

    override fun put(url: String, bitmap: Bitmap) {
        cache.put(url, bitmap)
    }

    override fun get(url: String): Bitmap? {
        return cache.get(url)
    }

    override fun clear() {
        cache.evictAll()
    }

  /*  private val cache = Collections.synchronizedMap(
        LinkedHashMap<String, Bitmap>(10, 1.5f, true)
    )//Last argument true for LRU ordering
    private var size: Long = 0//current allocated size

    private var limit: Long = 1000000//max memory in bytes

    init {
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4)
    }

    fun setLimit(new_limit: Long) {
        limit = new_limit
        Log.i(TAG, "MemoryCache will use up to " + limit.toDouble() / 1024.0 / 1024.0 + "MB")
    }

    operator fun get(id: String): Bitmap? {
        try {
            return if (!cache.containsKey(id)) null else cache[id]
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
            return null
        }

    }

    fun put(id: String, bitmap: Bitmap) {
        try {
            if (cache.containsKey(id))
                size -= getSizeInBytes(cache[id])
            cache.put(id,bitmap)
            size += getSizeInBytes(bitmap)
            checkSize()
        } catch (th: Throwable) {
            th.printStackTrace()
        }

    }

    private fun checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size)
        if (size > limit) {
            val iter = cache.entries.iterator()//least recently accessed item will be the first one iterated
            while (iter.hasNext()) {
                val entry = iter.next()
                size -= getSizeInBytes(entry.value)
                iter.remove()
                if (size <= limit)
                    break
            }
            Log.i(TAG, "Clean cache. New size " + cache.size)
        }
    }

    fun clear() {
        try {
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear()
            size = 0
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }

    }

    internal fun getSizeInBytes(bitmap: Bitmap?): Long {
        return if (bitmap == null) 0 else (bitmap.rowBytes * bitmap.height).toLong()
    }

    companion object {
        private val TAG = "MemoryCache"
    }*/

}
