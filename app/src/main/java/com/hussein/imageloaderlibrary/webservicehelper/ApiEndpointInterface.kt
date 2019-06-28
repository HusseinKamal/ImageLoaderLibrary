package com.hussein.imageloaderlibrary.webservicehelper

import com.hussein.imageloaderlibrary.model.Image
import io.reactivex.Observable
import retrofit2.http.GET
interface ApiEndpointInterface {
    @GET("raw/wgkJgazE")
    fun getImagesData(): Observable<List<Image>>

}