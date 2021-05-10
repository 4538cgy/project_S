package com.uos.smsmsm.recycleradapter.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.uos.smsmsm.R
import com.uos.smsmsm.data.TimeLineDTO

class PhotoAdapter(private val context : Context, private val photoList : ArrayList<String> ) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_content_photo, parent, false
            )
        )

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val requestOptions = RequestOptions
            .skipMemoryCacheOf(false)//memory cache 사용
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        if (photoList.size > 0 ) {
            println("이미지 꽂아넣기")
            Glide.with(context)
                .load(photoList[position])
                .dontAnimate()
                .placeholder(R.drawable.ic_baseline_bookmarks_24)
                .override(Target.SIZE_ORIGINAL)
                .apply(requestOptions)
                .thumbnail(
                    Glide.with(context).load(photoList[position]).fitCenter()
                )
                .into(holder.image)
        }else{
            println("이미지 지우기")
            Glide.with(context).clear(holder.image)
            holder.image.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            //두번 연속 클릭시 좋아요 이벤트
        }
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image : ImageView = view.findViewById(R.id.item_content_photo_imageview)
    }



}