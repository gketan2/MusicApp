package com.k10.musicapp.ui.main

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.k10.musicapp.R
import com.k10.musicapp.services.PlayerService
import com.k10.musicapp.services.PlayerState
import com.k10.musicapp.ui.BaseActivity
import com.k10.musicapp.ui.player.PlayerActivity
import com.k10.musicapp.utils.CommandOrigin
import com.k10.musicapp.utils.Constants.Companion.THEME_DARK
import com.k10.musicapp.utils.Constants.Companion.THEME_LIGHT
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
        mainActivityMore.setOnClickListener(this)
    }

    fun subscribeObserver() {
        playerService?.let {
            //observing for song/singer name and poster
            it.getCurrentSongObject().observe(this, { song ->
                if (song != null) {
                    floatingBar.visibility = View.VISIBLE
                    floatingSongInfo.text = "${song.songName}-${song.singer}"
                    Glide.with(this)
                        .load(song.songPosterUrl)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(floatingImageView)
                } else {
                    floatingBar.visibility = View.GONE
                }
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
                if (isBounded) {
                    playerService?.requestPlayerService(
                        PlayerRequestType.PLAYPAUSE,
                        CommandOrigin.FLOATING_BAR
                    )
                }
            }
            R.id.floatingBar -> {
                val i = Intent(this, PlayerActivity::class.java)
                startActivity(i)
            }
            R.id.mainActivityMore -> {
                changeThemePopUp()
            }
        }
    }

    private fun changeThemePopUp() {
        val popup = PopupMenu(this, mainActivityMore)
        //inflating menu from xml resource
        popup.inflate(R.menu.more_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.theme -> {
                    changeTheme()
                }

            }
            return@setOnMenuItemClickListener true
        }
        popup.show();
    }

    private fun changeTheme() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_theme)

        val lightTheme: RadioButton = dialog.findViewById(R.id.dialogLightTheme)
        val darkTheme: RadioButton = dialog.findViewById(R.id.dialogDarkTheme)

        val currentTheme = getCurrentTheme()
        if (currentTheme == THEME_LIGHT) {
            lightTheme.isChecked = true
        } else {
            darkTheme.isChecked = true
        }

        lightTheme.setOnClickListener {
            if (currentTheme != THEME_LIGHT) {
                setCurrentTheme(THEME_LIGHT)
                dialog.dismiss()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
                overridePendingTransition(0, 0)
            }
        }

        darkTheme.setOnClickListener {
            if (currentTheme != THEME_DARK) {
                setCurrentTheme(THEME_DARK)
                dialog.dismiss()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
                overridePendingTransition(0, 0)
            }
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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