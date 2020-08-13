package com.k10.musicapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        networkStatus()
    }


    //Live Data to be observed in ViewModel for notify network Changes
    val networkConnected: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var connectivityManager: ConnectivityManager

    private val networkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkConnected.postValue(true)
        }

        override fun onLost(network: Network) {
            networkConnected.postValue(false)
        }
    }

    private fun networkStatus() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onTerminate() {
        super.onTerminate()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}