package com.uos.smsmsm.activity.content

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.databinding.ItemGalleryViewBinding
import com.uos.smsmsm.util.GalleryUtil.MediaItem
import com.uos.smsmsm.util.MediaType
import com.uos.smsmsm.util.dpToPx


class GalleryAdapter(var galleryLists: MutableList<MediaItem>, val context: Context, private val clickListener: View.OnClickListener) : RecyclerView.Adapter<GalleryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder =
       GalleryHolder( ItemGalleryViewBinding.inflate(LayoutInflater.from(context), parent, false) )


    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        holder.bind(galleryLists[position],clickListener, context)
    }

    override fun getItemCount(): Int = galleryLists.size

    override fun getItemViewType(position: Int): Int = position

}
class GalleryHolder(val binding : ItemGalleryViewBinding) : RecyclerView.ViewHolder(binding.root){

    private lateinit var mediaItem: MediaItem
    fun bind(item: MediaItem, clickListener: View.OnClickListener, context: Context){

        mediaItem = item
        binding.root.setOnClickListener(clickListener)
        if(mediaItem.isType == MediaType.Gallery){
            binding.itemGalleryLayoutImg.visibility = View.GONE
            binding.itemGalleryLayoutOpenGallery.visibility = View.VISIBLE
        }else {
            binding.itemGalleryLayoutImg.visibility = View.VISIBLE
            binding.itemGalleryLayoutOpenGallery.visibility = View.GONE
            Glide.with(context)
                .load(item.contentUri)
                .apply(
                    RequestOptions().centerCrop()
                )
                .into(binding.itemGalleryViewImg)
        }
    }

    fun getMediaItem() : MediaItem = mediaItem
}