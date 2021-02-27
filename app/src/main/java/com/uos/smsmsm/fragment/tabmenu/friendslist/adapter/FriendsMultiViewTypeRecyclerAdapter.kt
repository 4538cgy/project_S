package com.uos.smsmsm.fragment.tabmenu.friendslist.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uos.smsmsm.R
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.*
import java.lang.RuntimeException

class FriendsMultiViewTypeRecyclerAdapter(private val context: Context,friendsList : ArrayList<RecyclerDefaultModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var list : ArrayList<RecyclerDefaultModel> = arrayListOf()

    init{
        list = friendsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        println("뷰타이이이이이입" + viewType.toString())

        return when (viewType){
            RecyclerDefaultModel.TEXT_TYPE_2 -> {
                //TextType2ViewHolder(ItemMultiViewTextTypeBinding.inflate(LayoutInflater.from(context),parent,false))
                val binding = ItemMultiViewTextType2Binding.inflate(LayoutInflater.from(context),parent,false)
                return TextType2ViewHolder(binding)
            }
            RecyclerDefaultModel.TEXT_TYPE -> {
                val binding = ItemMultiViewTextTypeBinding.inflate(LayoutInflater.from(context),parent,false)
                return TextTypeViewHolder(binding)
            }
            RecyclerDefaultModel.IMAGE_TYPE -> {
                val binding = ItemMultiViewImageTypeBinding.inflate(LayoutInflater.from(context),parent,false)
                return ImageTypeViewHolder(binding)
            }
            RecyclerDefaultModel.IMAGE_TYPE_2 -> {
                val binding = ItemMultiViewImageType2Binding.inflate(LayoutInflater.from(context),parent,false)
                return ImageType2ViewHolder(binding)
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE -> {
                val binding = ItemMultiViewFriendsListTypeTitleBinding.inflate(LayoutInflater.from(context),parent,false)
                return FriendsListTypeTitleViewHolder(binding)
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT -> {
                val binding = ItemMultiViewFriendsListTypeTitleContentBinding.inflate(LayoutInflater.from(context),parent,false)
                return FriendsListTypeTitleContentViewHolder(binding)
            }
            else -> throw RuntimeException("nope")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //holder.onBind(list[position])

        val obj = list[position]
        when (obj.type){
            RecyclerDefaultModel.TEXT_TYPE_2 -> {
                (holder as TextType2ViewHolder).onBind(list[position])
                holder.binding.itemMultiViewTextType2TextviewTitle.text = list[position].title
            }
            RecyclerDefaultModel.TEXT_TYPE -> {
                (holder as TextTypeViewHolder).onBind(list[position])
                holder.binding.itemMultiViewTextTypeTextviewTitle.text = list[position].title
            }
            RecyclerDefaultModel.IMAGE_TYPE -> {
                (holder as ImageTypeViewHolder).onBind(list[position])
                holder.binding.itemMultiViewImageTypeTextviewTitle.text = list[position].title
                holder.binding.itemMultiViewImageTypeImageviewBackground.setImageResource(list[position].imageUrl!!)
            }
            RecyclerDefaultModel.IMAGE_TYPE_2 -> {
                (holder as ImageType2ViewHolder).onBind(list[position])
                holder.binding.itemMultiViewImageType2TextviewTitle.text = list[position].title
                holder.binding.itemMultiViewImageType2TextviewContent.text = list[position].content
                holder.binding.itemMultiViewImageType2ImageviewBackground.setImageResource(list[position].imageUrl!!)
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE -> {
                (holder as FriendsListTypeTitleViewHolder).onBind(list[position])
                holder.binding.itemMultiViewFriendsListTypeTitleTextviewTitle.text = list[position].title
                Glide.with(holder.itemView.context)
                    .load(list[position].downloadImageUrl)
                    .circleCrop()
                    .into(holder.binding.itemMultiViewFriendsListTypeTitleImageview)
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT ->{
                (holder as FriendsListTypeTitleContentViewHolder).onBind(list[position])
                holder.binding.itemMultiViewFriendsListTypeTitleContentTextviewTitle.text = list[position].title
                holder.binding.itemMultiViewFriendsListTypeTitleContentTextviewContent.text = list[position].content
                Glide.with(holder.itemView.context)
                    .load(list[position].downloadImageUrl)
                    .circleCrop()
                    .into(holder.binding.itemMultiViewFriendsListTypeTitleContentImageview)
            }
        }

    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    inner class TextType2ViewHolder(val binding : ItemMultiViewTextType2Binding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data: RecyclerDefaultModel){
            binding.itemmultiviewtexttype2 = data
        }
    }

    inner class TextTypeViewHolder(val binding : ItemMultiViewTextTypeBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data: RecyclerDefaultModel){
            binding.itemmultiviewtexttype = data
        }
    }

    inner class ImageTypeViewHolder(val binding : ItemMultiViewImageTypeBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : RecyclerDefaultModel){
            binding.itemmultiviewimagetype = data
        }
    }

    inner class ImageType2ViewHolder(val binding : ItemMultiViewImageType2Binding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : RecyclerDefaultModel){
            binding.itemmultiviewimagetype2 = data
        }
    }
    inner class FriendsListTypeTitleViewHolder(val binding : ItemMultiViewFriendsListTypeTitleBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : RecyclerDefaultModel){
            binding.itemmultiviewfriendslisttypetitle = data
        }
    }

    inner class FriendsListTypeTitleContentViewHolder(val binding : ItemMultiViewFriendsListTypeTitleContentBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : RecyclerDefaultModel){
            binding.itemmultiviewfriendslisttypetitlecontent = data
        }
    }




}