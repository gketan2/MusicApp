package com.k10.musicapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.observe
import com.k10.musicapp.BaseApplication
import com.k10.musicapp.R
import com.k10.musicapp.broadcast.CustomBroadcastReceiver
import com.k10.musicapp.ui.BaseActivity
import com.k10.musicapp.ui.main.MainActivity
import com.k10.musicapp.ui.player.PlayerActivity
import com.k10.musicapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*

@AndroidEntryPoint
class SplashActivity : BaseActivity(), View.OnClickListener {

    private val music_url = "https://mp3d.jamendo.com/?trackid=799037&format=mp32"

    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d(TAG, "onCreate")

        broadcast.setOnClickListener(this)
        play.setOnClickListener(this)
        pause.setOnClickListener(this)
        temp.setOnClickListener(this)

        (application as BaseApplication)
            .networkConnected.observe(this) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.play -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
            R.id.pause -> {
            }
            R.id.broadcast -> {
            }
            R.id.temp -> {
                val i = Intent(this, PlayerActivity::class.java)
                startActivity(i)
            }
        }
    }
}