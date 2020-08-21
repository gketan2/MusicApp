package com.k10.musicapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.lifecycle.MutableLiveData
import com.k10.musicapp.services.PlayerService
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //start Player Service as soon as the Application is started
        val i = Intent(this, PlayerService::class.java)
        startService(i)

        networkStatus()
    }

    //Live Data to be observed in ViewModel for notifying network changes
    val networkConnected: MutableLiveData<Boolean> = MutableLiveData()

    private var connectivityManager: ConnectivityManager? = null

    private val networkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkConnected.postValue(true)
        }

        override fun onLost(network: Network) {
            networkConnected.postValue(false)
        }
    }

    private fun networkStatus() {
        if (connectivityManager == null)
            connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager?.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onTerminate() {
        super.onTerminate()
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        connectivityManager = null

        //val i = Intent(this, PlayerService::class.java)
        //stopService(i)
    }
}