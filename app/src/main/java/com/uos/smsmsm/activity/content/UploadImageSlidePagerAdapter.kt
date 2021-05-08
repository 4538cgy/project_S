package com.uos.smsmsm.activity.content

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.databinding.ItemHeaderViewBinding
import com.uos.smsmsm.databinding.ItemUploadImageViewBinding
import com.uos.smsmsm.util.GalleryUtil.MediaItem
import com.uos.smsmsm.util.MediaType

class UploadImageSlidePagerAdapter(private val uploadList : ArrayList<UploadImgDTO>, private val listener: View.OnClickListener,val videoListener : View.OnClickListener,val context : Context) : RecyclerView.Adapter<UploadImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImageViewHolder = UploadImageViewHolder(
        ItemUploadImageViewBinding.inflate(
        LayoutInflater.from(context),
        parent,
        false)
    )

    override fun onBindViewHolder(holder: UploadImageViewHolder, position: Int) {
        holder.bind(uploadList[position],listener,videoListener,context)
    }

    override fun getItemCount(): Int = uploadList.size


}

class UploadImageViewHolder(val binding : ItemUploadImageViewBinding) : RecyclerView.ViewHolder(binding.root){

    var holder: GalleryHolder? = null // 하단 갤러리로 선택된 이미지
    var mediaItem : MediaItem? = null // 사진 촬영으로 선택된 이미지
    fun bind(item : UploadImgDTO, listener: View.OnClickListener,videoListener: View.OnClickListener, context : Context){
        holder = item.galleryHolder
        mediaItem = item.mediaItem
        holder?.let{

            if(it.getMediaItem().isType == MediaType.Video){
                binding.itemUploadImageViewPlayBtn.visibility = View.VISIBLE
            }
            Glide.with(context)
                .load(it.getMediaItem().contentUri)
                .apply(
                    RequestOptions().centerCrop()
                )
                .into(binding.itemUploadImageViewImg)
        }?: run {
            mediaItem?.let { it_mediaitem ->
                if(it_mediaitem.isType == MediaType.Video){
                    binding.itemUploadImageViewPlayBtn.visibility = View.VISIBLE
                }
                Glide.with(context)
                    .load(it_mediaitem.contentUri)
                    .apply(
                        RequestOptions().centerCrop()
                    )
                    .into(binding.itemUploadImageViewImg)
            }
        }

        binding.itemUploadImageViewCloseBtn.setOnClickListener(listener)
        binding.itemUploadImageViewPlayBtn.setOnClickListener (videoListener)
    }
}