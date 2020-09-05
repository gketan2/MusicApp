package com.k10.musicapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.k10.musicapp.R
import com.k10.musicapp.datamodel.SongObject
import kotlinx.android.synthetic.main.list_song.view.*

class SongListAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "SongListAdapter"

    /**
     * Code Related to DiffUtil
     */
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SongObject>() {

        override fun areItemsTheSame(oldItem: SongObject, newItem: SongObject): Boolean {
            return oldItem.songId == newItem.songId
        }

        override fun areContentsTheSame(oldItem: SongObject, newItem: SongObject): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    /**
     * Code related to RecyclerView
     */
    private var currentSong: SongObject? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SongListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_song,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SongListViewHolder -> {
                holder.bind(differ.currentList[position]!!)
                Log.d(TAG, "onBindViewHolder: $position")

                if (currentSong != null) {
                    if (differ.currentList[position] == currentSong) {
                        holder.itemView.background = ContextCompat.getDrawable(
                            holder.itemView.context,
                            R.drawable.selected_list_item_background
                        )
                    }
                }

                holder.itemView.songListSongName.text = differ.currentList[position].songName
                holder.itemView.songListSingerName.text = differ.currentList[position].singer
                Glide.with(holder.itemView.context)
                    .load(differ.currentList[position].songPosterUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.itemView.songListImageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: ArrayList<SongObject>) {
        differ.submitList(list)
    }

    fun setCurrentSong(song: SongObject?) {
        currentSong = song
    }
}

/**
 * ViewHolder for this adapter
 */
class SongListViewHolder constructor(
    itemView: View,
    private val interaction: Interaction?
) : RecyclerView.ViewHolder(itemView) {

    private val TAG = "SongListAdapter"
    fun bind(item: SongObject) {
        itemView.setOnClickListener {
            Log.d(TAG, "bind: $adapterPosition")
            interaction?.onItemSelected(adapterPosition, item)
        }
    }
}

interface Interaction {
    fun onItemSelected(position: Int, item: SongObject)
}