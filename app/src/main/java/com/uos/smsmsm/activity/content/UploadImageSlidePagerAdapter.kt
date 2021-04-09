package com.uos.smsmsm.activity.content

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.uos.smsmsm.databinding.ItemHeaderViewBinding
import com.uos.smsmsm.databinding.ItemUploadImageViewBinding
import com.uos.smsmsm.util.GalleryUtil.MediaItem

class UploadImageSlidePagerAdapter(private val uploadList : ArrayList<UploadImgDTO>, private val listener: View.OnClickListener,val context : Context) : RecyclerView.Adapter<UploadImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImageViewHolder = UploadImageViewHolder(
        ItemUploadImageViewBinding.inflate(
        LayoutInflater.from(context),
        parent,
        false)
    )

    override fun onBindViewHolder(holder: UploadImageViewHolder, position: Int) {
        holder.bind(uploadList[position],listener)
    }

    override fun getItemCount(): Int = uploadList.size


}

class UploadImageViewHolder(val binding : ItemUploadImageViewBinding) : RecyclerView.ViewHolder(binding.root){

    var holder: GalleryHolder? = null // 하단 갤러리로 선택된 이미지
    var mediaItem : MediaItem? = null // 사진 촬영으로 선택된 이미지
    fun bind(item : UploadImgDTO, listener: View.OnClickListener){
        holder = item.galleryHolder
        mediaItem = item.mediaItem
        holder?.let{
            binding.itemUploadImageViewImg.setImageURI(it.getMediaItem().contentUri)
        }?: run {
            mediaItem?.let { it_mediaitem ->
                binding.itemUploadImageViewImg.setImageURI(it_mediaitem.contentUri)
            }
        }
        binding.itemUploadImageViewCloseBtn.setOnClickListener(listener)
    }
}