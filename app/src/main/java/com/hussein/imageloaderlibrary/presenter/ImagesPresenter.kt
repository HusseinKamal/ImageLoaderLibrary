package com.hussein.imageloaderlibrary.presenter

import com.hussein.imageloaderlibrary.listener.IDataPresenter
import com.hussein.imageloaderlibrary.model.Image
import com.hussein.imageloaderlibrary.webservicehelper.ApiEndPoint
import com.hussein.imageloaderlibrary.webservicehelper.ApiEndpointInterface
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ImagesPresenter (mListener:IDataPresenter)
{
    var listener:IDataPresenter=mListener

    fun loadDataImages() {
        try {
            val objApiEndpointInterface = ApiEndPoint.client()!!.create(
                ApiEndpointInterface::class.java)
            objApiEndpointInterface.getImagesData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Image>> {
                    override fun onNext(images: List<Image>) {
                        try {
                            if (images.isNotEmpty()) {
                                listener.bindData(images)
                            }
                            else
                            {
                                listener.emptyData()
                            }
                        } catch (e: Exception) {
                            listener.emptyData()
                            e.printStackTrace()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(ex: Throwable) {
                        listener.emptyData()
                        ex.printStackTrace()
                    }

                    override fun onComplete() {
                    }
                })
        }
        catch (e:Exception)
        {
            listener.emptyData()
            e.printStackTrace()
        }
    }
}