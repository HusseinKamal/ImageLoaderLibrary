package com.hussein.imageloaderlibrary.model

import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("urls")
    var urls: Image?=null
        get() = field
        set(value) { field = value}

    @SerializedName("raw")
    var raw: String=""
        get() = field
        set(value) { field = value}
}