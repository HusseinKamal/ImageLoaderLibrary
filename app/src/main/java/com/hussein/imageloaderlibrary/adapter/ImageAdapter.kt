package com.hussein.imageloaderlibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hussein.imageload.ImageLoad
import com.hussein.imageload.ImageLoader
import com.hussein.imageloaderlibrary.R
import com.hussein.imageloaderlibrary.model.Image
import kotlinx.android.synthetic.main.item_image_layout.view.*

class ImageAdapter (context: Context,items :List<Image>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var mContext=context
    var items=items
    lateinit var img:ImageLoader
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.item_image_layout,
                p0,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItems = items[position]
        img= ImageLoader(mContext)
        //ImageLoad.placehoder(R.drawable.ic_photo)
        //ImageLoad.error(R.drawable.ic_launcher_background)
        img.displayImage(listItems.urls!!.raw, holder.ivPhoto)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val ivPhoto = view.ivPhoto
    }
}