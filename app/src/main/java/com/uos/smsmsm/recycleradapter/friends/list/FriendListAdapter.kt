package com.uos.smsmsm.recycleradapter.friends.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ItemMultiViewFriendsListTypeTitleContentBinding
import com.uos.smsmsm.recycleradapter.diffCallback.FriendsListDiffCallback
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.util.Delegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// 베이스를 안쓴 이유는 자꾸 새리스트 받아 올 경우 데이터가 이상하게 변경이 된다.
class FriendListAdapter(private val callback: Delegate.Action1<RecyclerDefaultModel>) :
    RecyclerView.Adapter<FriendListViewHolder>() {

    private val asyncDiffer = AsyncListDiffer(this, FriendsListDiffCallback())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendListViewHolder {
        val binding = ItemMultiViewFriendsListTypeTitleContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendListViewHolder(binding = binding, callback)
    }
    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        holder.bind(asyncDiffer.currentList[position])
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size
    fun replaceList(newList: ArrayList<RecyclerDefaultModel>){
        asyncDiffer.submitList(newList)
    }
}


class FriendListViewHolder(
    private val binding: ItemMultiViewFriendsListTypeTitleContentBinding,
    private val callback: Delegate.Action1<RecyclerDefaultModel>
) :
    RecyclerView.ViewHolder(binding.root){


    private val userRepository = UserRepository()
    private val ioScope = CoroutineScope(Dispatchers.Main)
    private lateinit var element: RecyclerDefaultModel
    init {


    }

    fun bind(element: RecyclerDefaultModel) {
        this.element = element

        binding.itemMultiViewFriendsListTypeTitleContentMoreBtn.setOnCreateContextMenuListener { menu, v, menuInfo ->
            menu?.let{
                it.add(Menu.NONE, 0, 0, binding.root.context.getString(R.string.remove)).apply {
                    setOnMenuItemClickListener {
                        callback.run(element)
                        true
                    }
                }
            }

        }
        //  아이템 자체 클릭
        itemView.setOnClickListener {
            binding.root.context.startActivity(
                Intent(
                    binding.root.context,
                    ChatActivity::class.java
                ).putExtra("destinationUid",element.uid)
            )
        }
        binding.itemMultiViewFriendsListTypeTitleContentTextviewTitle.text = element.title
        binding.itemMultiViewFriendsListTypeTitleContentTextviewContent.text = element.content

        //프로필 이미지 클릭
        binding.itemMultiViewFriendsListTypeTitleContentImageview.setOnClickListener {
            Intent(binding.root.context, ProfileActivity::class.java).run {
                putExtra("uid", element.uid)
                binding.root.context.startActivity(this)
            }
        }


        //프로필 이미지 클릭
//        binding.itemMultiViewFriendsListTypeTitleContentImageview.setOnClickListener {
//            ioScope.launch {
//                userRepository.getUserProfileImage(element.uid!!).collect {
//
//                    var intent = Intent(binding.root.context, PhotoViewActivity::class.java)
//                    intent.apply {
//                        putExtra("imageUrl", it)
//                        binding.root.context.startActivity(intent)
//                    }
//                }
//            }
//        }
        //프로필 이미지 출력
        ioScope.launch {
            userRepository.getUserProfileImage(element.uid!!).collect {
                Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop())
                    .into(binding.itemMultiViewFriendsListTypeTitleContentImageview)
            }
        }
    }
}

