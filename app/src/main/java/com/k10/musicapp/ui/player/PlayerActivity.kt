package com.k10.musicapp.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.k10.musicapp.R
import com.k10.musicapp.helper.CustomAnimation
import com.k10.musicapp.services.PlayerService
import com.k10.musicapp.services.PlayerState
import com.k10.musicapp.ui.BaseActivity
import com.k10.musicapp.utils.CommandOrigin
import com.k10.musicapp.utils.PlayerRequestType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_player.*
import soup.neumorphism.NeumorphImageButton

@AndroidEntryPoint
class PlayerActivity : BaseActivity(), View.OnClickListener {
    private val TAG = "PlayerActivity"

    //Instance of playerService
    //will be NULL if Service is NOT bounded to this Activity
    private var playerService: PlayerService? = null
    private var isBounded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        setClickListeners()
    }

    private fun subscribeObserver() {
        playerService?.let {
            //Observing MusicPlayer State
            it.getPlayerStateLiveData().observe(this, { wrapper ->
                if (wrapper.status != PlayerState.PLAYING)
                    playerSongPoster.clearAnimation()
                when (wrapper.status) {
                    PlayerState.NONE -> {
                    }
                    PlayerState.ERROR -> {
                        playerPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                        Toast.makeText(this, wrapper.message, Toast.LENGTH_SHORT).show()
                    }
                    PlayerState.IDLE -> {
                    }
                    PlayerState.INITIALIZED -> {
                    }
                    PlayerState.PREPARING -> {
                        playerPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_pause
                            )
                        )
                    }
                    PlayerState.PREPARED -> {

                    }
                    PlayerState.PLAYING -> {
                        playerSongPoster.startAnimation(CustomAnimation.rotateAroundCentre(15000))
                        playerPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_pause
                            )
                        )
                    }
                    PlayerState.PAUSED -> {
                        playerPlayPause.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_play
                            )
                        )
                    }
                    PlayerState.STOPPED -> {
                    }
                    PlayerState.PLAYBACK_COMPLETE -> {
                    }
                }
            })

            //Observing and Updating Seek Time to the TextFields
            it.getCurrentSeekLiveData().observe(this, { pO ->
                playerCurrentSeek.text = "${pO.playedMinute}:%02d".format(pO.playedSecond)
                playerTotalSeek.text = "${pO.durationMinute}:%02d".format(pO.durationSecond)
                playerSeekBar.progress =
                    pO.playedMilli * 1000 / (pO.durationMilli + 1)//+1 -> to handle durationmilli == 0
            })
        }
    }

    fun unsubscribeObserver() {
        playerService?.getPlayerStateLiveData()?.removeObservers(this)
        playerService?.getCurrentSeekLiveData()?.removeObservers(this)
    }

    private fun setClickListeners() {
        playerBackButton.setOnClickListener(this)
        playerNext.setOnClickListener(this)
        playerPrevious.setOnClickListener(this)
        playerPlayPause.setOnClickListener(this)

        playerSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
        playerPlayPause.setOnTouchListener(onTouchListener)
        playerPrevious.setOnTouchListener(onTouchListener)
        playerNext.setOnTouchListener(onTouchListener)
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
            R.id.playerBackButton -> {
                onBackPressed()
            }
            R.id.playerPlayPause -> {
                Log.d(TAG, "onClick: playPauseClick")
                if (isBounded) {
                    playerService?.requestPlayerService(
                        PlayerRequestType.PLAYPAUSE,
                        CommandOrigin.PLAYER_UI
                    )
                }
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

    //SeekBarChangeListener to listen when user has changed the SeekBar position
    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        var position = 0
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                position = progress
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if(isBounded){
                Log.d(TAG, "onStopTrackingTouch: $position")
                playerService?.seekPlaybackTo(position)
            }
        }
    }

    //OnTouchListener for Play/Pause,Next,Previous NeumorphImageButton
    //Used to Implement Pressed-Animation
    private val onTouchListener = View.OnTouchListener { v, event ->
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                (v as NeumorphImageButton).setShapeType(1)
            } else if (it.action == MotionEvent.ACTION_UP) {
                (v as NeumorphImageButton).setShapeType(0)
                v.performClick()
            }
        }
        true
    }
}