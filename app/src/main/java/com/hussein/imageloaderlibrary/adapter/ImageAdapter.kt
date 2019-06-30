package com.hussein.imageloaderlibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hussein.imageload.ImageLoader
import com.hussein.imageloaderlibrary.R
import com.hussein.imageloaderlibrary.model.Image
import kotlinx.android.synthetic.main.item_image_layout.view.*

class ImageAdapter (context: Context,items :List<Image>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    var mContext=context
    var items=items
    //lateinit var img:ImageLoader
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_image_layout, p0, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItems = items[position]
        ImageLoader(mContext).setError(R.drawable.ic_empty).placeholder(R.drawable.ic_photo).resize(200,200).DisplayImage(listItems.urls!!.raw, holder.ivPhoto)
        holder.ivPhoto.setOnClickListener {
            Toast.makeText(mContext.applicationContext, listItems.urls!!.raw, Toast.LENGTH_SHORT).show()
        }
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