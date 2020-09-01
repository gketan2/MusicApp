package com.k10.musicapp.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.k10.musicapp.R
import com.k10.musicapp.services.PlayerService
import com.k10.musicapp.services.PlayerState
import com.k10.musicapp.ui.BaseActivity
import com.k10.musicapp.ui.player.PlayerActivity
import com.k10.musicapp.utils.CommandOrigin
import com.k10.musicapp.utils.PlayerRequestType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_floating_bar.*

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener {
    private val TAG = "MainActivity"

    //Instance of playerService
    //will be NULL if Service is NOT bounded to this Activity
    private var playerService: PlayerService? = null
    private var isBounded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        floatingPlayPause.setOnClickListener(this)
        floatingBar.setOnClickListener(this)
    }

    fun subscribeObserver() {
        playerService?.let {
            //observing for song/singer name and poster
            it.getCurrentSongObject().observe(this, {song ->
                floatingSongInfo.text = "${song.songName}-${song.singer}"
                Glide.with(this)
                    .load(song.songPosterUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(floatingImageView)
            })
            //observing current state for playpause button
            it.getPlayerStateLiveData().observe(this, { wrapper ->
                when (wrapper.status) {
                    PlayerState.NONE -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                    PlayerState.ERROR -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                        Toast.makeText(this, wrapper.message, Toast.LENGTH_SHORT).show()
                    }
                    PlayerState.IDLE -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                    PlayerState.INITIALIZED -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                    PlayerState.PREPARING -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_pause
                            )
                        )
                    }
                    PlayerState.PREPARED -> {
                    }
                    PlayerState.PLAYING -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_pause
                            )
                        )
                    }
                    PlayerState.PAUSED -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                    PlayerState.STOPPED -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                    PlayerState.PLAYBACK_COMPLETE -> {
                        floatingPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                }
            })
        }
    }

    fun unsubscribeObserver() {
        playerService?.getCurrentSongObject()?.removeObservers(this)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, PlayerService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
            Log.d(TAG, "onStart: binding service")
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBounded = false
        Log.d(TAG, "onStop: unbinding service")
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.floatingPlayPause -> {
                if(isBounded){
                    playerService?.requestPlayerService(PlayerRequestType.PLAYPAUSE, CommandOrigin.FLOATING_BAR)
                }
            }
            R.id.floatingBar -> {
                val i = Intent(this, PlayerActivity::class.java)
                startActivity(i)
            }
        }
    }

    //Callback for binding this activity to Service(PlayerService)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PlayerService.PlayerServiceBinder
            playerService = binder.getService()
            isBounded = true
            subscribeObserver()
            Log.d(TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBounded = false
            unsubscribeObserver()
        }
    }
}