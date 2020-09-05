package com.k10.musicapp.ui.main.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.k10.musicapp.R
import com.k10.musicapp.adapter.Interaction
import com.k10.musicapp.adapter.SongListAdapter
import com.k10.musicapp.datamodel.SongObject
import com.k10.musicapp.helper.FakeSongObject
import com.k10.musicapp.services.PlayerService
import com.k10.musicapp.utils.RecyclerDecorator
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), Interaction {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recyclerAdapter: SongListAdapter

    private val TAG = "HomeFragment"
    private var playerService: PlayerService? = null
    private var isBounded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        songRecycler.apply{
            layoutManager = LinearLayoutManager(this@HomeFragment.context)
            addItemDecoration(RecyclerDecorator(16))
            recyclerAdapter = SongListAdapter(this@HomeFragment)
            adapter = recyclerAdapter
        }
        recyclerAdapter.submitList(FakeSongObject.getSongList())
    }

    override fun onStart() {
        super.onStart()
        Intent(context, PlayerService::class.java).also {
            context?.bindService(it, connection, Context.BIND_AUTO_CREATE)
            Log.d(TAG, "onStart: binding service")
        }
    }

    override fun onStop() {
        super.onStop()
        context?.unbindService(connection)
        isBounded = false
        Log.d(TAG, "onStop: unbinding service")
    }

    override fun onItemSelected(position: Int, item: SongObject) {
        playerService?.playThisSong(item)
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

    private fun subscribeObserver(){
        if(isBounded){
            playerService?.let{
                it.getCurrentSongObject().observe(viewLifecycleOwner){song ->
                    Log.d(TAG, "subscribeObserver: setting current song in adapter")
                    recyclerAdapter.setCurrentSong(song)
                }
            }
        }
    }

    private fun unsubscribeObserver(){
        playerService?.getCurrentSongObject()!!.removeObservers(viewLifecycleOwner)
    }
}