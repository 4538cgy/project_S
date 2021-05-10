package com.uos.smsmsm.recycleradapter.viewpager

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.uos.smsmsm.R
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.util.dialog.LoadingDialog

class PhotoAdapter(private val context : Context, private val photoList : ArrayList<String> ) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    lateinit var loadingDialog: LoadingDialog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_content_photo, parent, false
            )
        )

    override fun getItemCount(): Int = photoList.size

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        //프로그레스 초기화
        loadingDialog = LoadingDialog(context).apply {
            //프로그레스 투명하게
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //프로그레스 꺼짐 방지
            setCancelable(false)
        }

        if (photoList.size > 0 ) {
            println("이미지 꽂아넣기")
            Glide.with(context)
                .load(photoList[position])
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        println("로드 실패!!!!")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        println("로드 성공!!!!!")
                        return false
                    }

                })
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