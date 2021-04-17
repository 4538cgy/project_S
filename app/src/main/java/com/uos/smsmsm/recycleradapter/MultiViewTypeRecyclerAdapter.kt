package com.uos.smsmsm.recycleradapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ItemMultiViewFriendsListTypeTitleBinding
import com.uos.smsmsm.databinding.ItemMultiViewFriendsListTypeTitleContentBinding
import com.uos.smsmsm.databinding.ItemMultiViewImageType2Binding
import com.uos.smsmsm.databinding.ItemMultiViewImageTypeBinding
import com.uos.smsmsm.databinding.ItemMultiViewTextType2Binding
import com.uos.smsmsm.databinding.ItemMultiViewTextTypeBinding
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.ui.photo.PhotoViewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// (friendslist) list init in constructor, ArrayList -> List (array -> collection)
class MultiViewTypeRecyclerAdapter(
    private val context: Context,
    private val list : LiveData<ArrayList<RecyclerDefaultModel>>

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val userRepository = UserRepository()
    val ioScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {



        // Remove return in return
        return when (viewType) {

            RecyclerDefaultModel.TEXT_TYPE_2 -> {
                // TextType2ViewHolder(ItemMultiViewTextTypeBinding.inflate(LayoutInflater.from(context),parent,false))
                val binding = ItemMultiViewTextType2Binding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                TextType2ViewHolder(binding)
            }
            RecyclerDefaultModel.TEXT_TYPE -> {
                val binding = ItemMultiViewTextTypeBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                TextTypeViewHolder(binding)
            }
            RecyclerDefaultModel.IMAGE_TYPE -> {
                val binding = ItemMultiViewImageTypeBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                ImageTypeViewHolder(binding)
            }
            RecyclerDefaultModel.IMAGE_TYPE_2 -> {
                val binding = ItemMultiViewImageType2Binding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                ImageType2ViewHolder(binding)
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE -> {
                val binding = ItemMultiViewFriendsListTypeTitleBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                FriendsListTypeTitleViewHolder(binding)
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT -> {
                val binding = ItemMultiViewFriendsListTypeTitleContentBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
                FriendsListTypeTitleContentViewHolder(binding)
            }
            else -> throw RuntimeException("nope")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // holder.onBind(list[position])

        val obj = list.value!![position]
        when (obj.type) {
            RecyclerDefaultModel.TEXT_TYPE_2 -> {
                (holder as TextType2ViewHolder).onBind(list.value!![position])
                holder.binding.itemMultiViewTextType2TextviewTitle.text = list.value!![position].title
            }
            RecyclerDefaultModel.TEXT_TYPE -> {
                (holder as TextTypeViewHolder).onBind(list.value!![position])
                holder.binding.itemMultiViewTextTypeTextviewTitle.text = list.value!![position].title
            }
            RecyclerDefaultModel.IMAGE_TYPE -> {
                (holder as ImageTypeViewHolder).onBind(list.value!![position])
                holder.binding.itemMultiViewImageTypeTextviewTitle.text = list.value!![position].title
                holder.binding.itemMultiViewImageTypeImageviewBackground.setImageResource(list.value!![position].imageUrl!!)
            }
            RecyclerDefaultModel.IMAGE_TYPE_2 -> {
                (holder as ImageType2ViewHolder).onBind(list.value!![position])
                holder.binding.itemMultiViewImageType2TextviewTitle.text = list.value!![position].title
                holder.binding.itemMultiViewImageType2TextviewContent.text = list.value!![position].content
                holder.binding.itemMultiViewImageType2ImageviewBackground.setImageResource(list.value!![position].imageUrl!!)
            }
            // Glide -> GlideApp (미세한 화질 증진용?)
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE -> {
                (holder as FriendsListTypeTitleViewHolder).onBind(list.value!![position])
                holder.binding.itemMultiViewFriendsListTypeTitleTextviewTitle.text =
                    list.value!![position].title


                //아이템 자체 클릭
                holder.itemView.setOnClickListener {
                    var intent = Intent(holder.binding.root.context,ProfileActivity::class.java)
                    intent.apply {
                        putExtra("uid", list.value!![position].uid)

                        holder.binding.root.context.startActivity(intent)
                    }
                }

                //프로필 이미지 클릭
                holder.binding.itemMultiViewFriendsListTypeTitleImageview.setOnClickListener {
                    ioScope.launch {
                        userRepository.getUserProfileImage(list.value!![position].uid!!).collect{

                            var intent = Intent(holder.binding.root.context,PhotoViewActivity::class.java)
                            intent.apply {
                                putExtra("imageUrl",it)
                                holder.binding.root.context.startActivity(intent)
                            }            }
                    }

                }

                //프로필 이미지 출력
                ioScope.launch {
                    userRepository.getUserProfileImage(list.value!![position].uid!!).collect{

                        Glide.with(holder.binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(holder.binding.itemMultiViewFriendsListTypeTitleImageview)
                    }
                }
            }
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT -> {
                (holder as FriendsListTypeTitleContentViewHolder).onBind(list.value!![position])
                holder.itemView.setOnClickListener {
                    holder.binding.root.context.startActivity(Intent(holder.binding.root.context,ChatActivity::class.java))
                }
                holder.binding.itemMultiViewFriendsListTypeTitleContentTextviewTitle.text =
                    list.value!![position].title
                holder.binding.itemMultiViewFriendsListTypeTitleContentTextviewContent.text =
                    list.value!![position].content

                //아이템 자체 클릭
                holder.itemView.setOnClickListener { holder.binding.root.context.startActivity(Intent(holder.binding.root.context,ProfileActivity::class.java)) }


                //프로필 이미지 클릭
                holder.binding.itemMultiViewFriendsListTypeTitleContentImageview.setOnClickListener {
                    ioScope.launch {
                        userRepository.getUserProfileImage(list.value!![position].uid!!).collect{

                            var intent = Intent(holder.binding.root.context,PhotoViewActivity::class.java)
                            intent.apply {
                                putExtra("imageUrl",it)
                                holder.binding.root.context.startActivity(intent)
                            }            }
                    }
                }

                //프로필 이미지 출력
                ioScope.launch {
                    userRepository.getUserProfileImage(list.value!![position].uid!!).collect{
                        Glide.with(holder.binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(holder.binding.itemMultiViewFriendsListTypeTitleContentImageview)
                    }
                }


            }
        }
    }

    override fun getItemCount() = list.value!!.size

    override fun getItemViewType(position: Int) = list.value!![position].type

    inner class TextType2ViewHolder(val binding: ItemMultiViewTextType2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecyclerDefaultModel) {
            binding.itemmultiviewtexttype2 = data
        }
    }

    inner class TextTypeViewHolder(val binding: ItemMultiViewTextTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecyclerDefaultModel) {
            binding.itemmultiviewtexttype = data
        }
    }

    inner class ImageTypeViewHolder(val binding: ItemMultiViewImageTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecyclerDefaultModel) {
            binding.itemmultiviewimagetype = data
        }
    }

    inner class ImageType2ViewHolder(val binding: ItemMultiViewImageType2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecyclerDefaultModel) {
            binding.itemmultiviewimagetype2 = data
        }
    }

    inner class FriendsListTypeTitleViewHolder(val binding: ItemMultiViewFriendsListTypeTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecyclerDefaultModel) {
            binding.itemmultiviewfriendslisttypetitle = data
        }
    }

    inner class FriendsListTypeTitleContentViewHolder(val binding: ItemMultiViewFriendsListTypeTitleContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: RecyclerDefaultModel) {
            binding.itemmultiviewfriendslisttypetitlecontent = data
        }
    }
}
