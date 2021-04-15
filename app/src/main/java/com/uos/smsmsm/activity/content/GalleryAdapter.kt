package com.uos.smsmsm.activity.content

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.databinding.ItemGalleryViewBinding
import com.uos.smsmsm.util.GalleryUtil.MediaItem


class GalleryAdapter(var galleryLists: MutableList<MediaItem>, val context: Context, private val clickListener: View.OnClickListener) : RecyclerView.Adapter<GalleryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder =
       GalleryHolder( ItemGalleryViewBinding.inflate(LayoutInflater.from(context), parent, false) )


    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        holder.bind(galleryLists[position],clickListener)
    }

    override fun getItemCount(): Int = galleryLists.size



}
class GalleryHolder(val binding : ItemGalleryViewBinding) : RecyclerView.ViewHolder(binding.root){

    private lateinit var mediaItem: MediaItem
    fun bind(item : MediaItem,clickListener: View.OnClickListener){
        mediaItem = item
        binding.root.setOnClickListener(clickListener)
        binding.itemGalleryViewImg.setImageURI(item.contentUri)
    }

    fun getMediaItem() : MediaItem = mediaItem
}