package com.uos.smsmsm.activity.content

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.uos.smsmsm.util.GalleryUtil.MediaItem

class UploadImageSlidePagerAdapter(val fa: AppCompatActivity,val uploadList : ArrayList<MediaItem>) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = uploadList.size
    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
    override fun createFragment(position: Int): UploadImageFragment = UploadImageFragment(uploadList[position])
    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val fragment  = fa.supportFragmentManager.findFragmentByTag("f$position")
        fragment?.let{
            if( it is UploadImageFragment){
                it.updateItem(uploadList[position])
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun update(){

        notifyDataSetChanged()
    }

}