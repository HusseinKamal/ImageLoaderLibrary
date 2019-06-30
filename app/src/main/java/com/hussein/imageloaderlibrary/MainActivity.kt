package com.hussein.imageloaderlibrary

import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import com.hussein.imageloaderlibrary.adapter.ImageAdapter
import com.hussein.imageloaderlibrary.listener.IDataPresenter
import com.hussein.imageloaderlibrary.model.Image
import com.hussein.imageloaderlibrary.network.Network
import com.hussein.imageloaderlibrary.presenter.ImagesPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_no_data.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,IDataPresenter{
    private lateinit var linearLayoutManager: LinearLayoutManager
    companion object {
        lateinit var adapter: ImageAdapter
    }
    private lateinit var presenter:ImagesPresenter

    override fun onRefresh() {
        try {
            swipeLoad.isRefreshing=false
            getData()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun bindData(images: List<Image>) {
        try {
            fillData(images)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun emptyData() {
        try {
            hideViews()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        getData()
    }
    override fun onDestroy() {
        super.onDestroy()
        //ImageLoad.clearCache()
    }
    private fun initViews()
    {
        try {
            ViewCompat.setNestedScrollingEnabled(rvImage, false)
            pbarLoad.visibility= View.GONE
            linearLayoutManager =
                object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                    override fun smoothScrollToPosition(
                        recyclerView: RecyclerView,
                        state: RecyclerView.State?,
                        position: Int
                    ) {
                        // A good idea would be to create this instance in some initialization method, and just set the target position in this method.
                        val smoothScroller = object : LinearSmoothScroller(this@MainActivity) {
                            override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
                                val yDelta = calculateCurrentDistanceToPosition(
                                    linearLayoutManager,
                                    targetPosition
                                )
                                return PointF(0f, yDelta.toFloat())
                            }

                            // This is the important method. This code will return the amount of time it takes to scroll 1 pixel.
                            // This code will request X milliseconds for every Y DP units.
                            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {

                                return displayMetrics.widthPixels / TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    displayMetrics.heightPixels.toFloat(),
                                    displayMetrics
                                )
                            }

                        }
                        smoothScroller.targetPosition = position

                        startSmoothScroll(smoothScroller)
                    }
                }
            swipeLoad.setOnRefreshListener(this)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    private fun calculateCurrentDistanceToPosition(mLayoutManager: LinearLayoutManager, targetPosition: Int): Int {
        val targetScrollY = targetPosition * 370
        return targetScrollY - mLayoutManager.findLastCompletelyVisibleItemPosition()
    }
    private fun getData(){
        try {
           if(Network.isOnline(this))
           {
               getImages()
           }
           else
           {
               pbarLoad.visibility= View.GONE
               rvImage.visibility= View.GONE
               tvTitle.text = resources.getString(R.string.no_internet_connection)
               ivNoData.setImageResource(R.drawable.ic_no_internet_connection)
               lyNoData.visibility=View.VISIBLE
           }
        }
        catch (e:Exception)
        {e.printStackTrace()}
    }
    private fun getImages(){
        try {
            presenter=ImagesPresenter(this@MainActivity)
            presenter.loadDataImages()
        }
        catch (e:Exception)
        {
            hideViews()
            e.printStackTrace()
        }
    }
    private fun fillData(images:List<Image>){
        try {
            rvImage.layoutManager = linearLayoutManager
            adapter=ImageAdapter(this, images)
            rvImage.adapter =adapter
            pbarLoad.visibility=View.GONE
            rvImage.visibility=View.VISIBLE
            lyNoData.visibility=View.GONE
        }
        catch (e:Exception)
        {e.printStackTrace()}
    }
    private fun hideViews(){
        try {
            pbarLoad.visibility=View.GONE
            rvImage.visibility=View.GONE
            lyNoData.visibility=View.VISIBLE
        }
        catch (e:Exception)
        {e.printStackTrace()}
    }
}
