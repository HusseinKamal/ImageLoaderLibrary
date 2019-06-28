package com.hussein.imageloaderlibrary.listener

import com.hussein.imageloaderlibrary.model.Image

interface IDataPresenter {
    fun bindData(images:List<Image>)
    fun emptyData()
}