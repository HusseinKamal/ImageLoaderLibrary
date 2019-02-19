package com.hussein.imageloaderlibrary


import java.io.File
import android.content.Context
import android.os.Environment

class FileCache(context: Context) {

    private var cacheDir: File? = null

    init {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState() == Environment.DIRECTORY_PICTURES)
            cacheDir = File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache")
        else
            cacheDir = context.cacheDir
        if (!cacheDir!!.exists())
            cacheDir!!.mkdirs()
    }

    fun getFile(url: String): File {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        val filename = url.hashCode().toString()
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        return File(cacheDir, filename)

    }

    fun clear() {
        val files = cacheDir!!.listFiles() ?: return
        for (f in files)
            f.delete()
    }

}