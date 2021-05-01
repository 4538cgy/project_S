package com.uos.smsmsm.recycleradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.uos.smsmsm.R

class PhotoAdapter(private val context : Context, private val photoList : ArrayList<String> ) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(
        R.layout.item_content_photo,parent,false))

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(photoList[position])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(
                Glide.with(context).load(photoList[position]).fitCenter()
            )
            .into(holder.image)

        holder.itemView.setOnClickListener {
            //두번 연속 클릭시 좋아요 이벤트
        }
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image : ImageView = view.findViewById(R.id.item_content_photo_imageview)
    }

}