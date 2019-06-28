package com.hussein.imageloaderlibrary.network

import android.content.Context
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.net.ConnectivityManager



class Network {
    companion object {
        fun isOnline(context: Context): Boolean {
            val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = conMgr.activeNetworkInfo

            return !(netInfo == null || !netInfo.isConnected || !netInfo.isAvailable)
        }
    }
}