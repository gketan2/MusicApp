package com.k10.musicapp.ui.splash

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.k10.musicapp.BaseApplication
import com.k10.musicapp.R
import com.k10.musicapp.services.PlayerService
import com.k10.musicapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var temp: Button
    private lateinit var textView: TextView

    private lateinit var playerService: PlayerService
    private var isBounded: Boolean = false
    private val music_url = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"

    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        playButton = findViewById(R.id.play)
        pauseButton = findViewById(R.id.pause)
        stopButton = findViewById(R.id.seek)
        temp = findViewById(R.id.temp)
        textView = findViewById(R.id.text)

        playButton.setOnClickListener(this)
        pauseButton.setOnClickListener(this)
        stopButton.setOnClickListener(this)
        temp.setOnClickListener(this)

        (application as BaseApplication)
            .networkConnected.observe(this) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, PlayerService::class.java).also{
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
        when(v?.id){
            R.id.play -> {
                if (isBounded) {
                    playerService.playThisSong(music_url)
                    Log.d(TAG, "playing this song")
                }
            }
            R.id.pause -> {
                if(isBounded){
                    playerService.pausePlayback()
                }
            }
            R.id.seek -> {
                if(isBounded){
                    playerService.seekPlayback()
                }
            }
            R.id.temp -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
    }

    //Callback for binding this activity to PlayerService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PlayerService.PlayerServiceBinder
            playerService = binder.getService()
            isBounded = true
            Log.d(TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBounded = false
        }
    }
}