package com.hussein.imageloaderlibrary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hussein.imageload.ImageLoad
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var img:ImageLoad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //ImageLoad.setCache(DoubleCache(applicationContext))
        img=ImageLoad(this)
        ImageLoad.displayImage("https://pbs.twimg.com/profile_images/1087478000826961921/F5n7FJuB_400x400.jpg", ivPhoto)
    }
    override fun onDestroy() {
        super.onDestroy()
        //ImageLoad.clearCache()
    }
}
