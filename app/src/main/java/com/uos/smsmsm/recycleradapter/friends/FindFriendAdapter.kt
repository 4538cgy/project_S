package com.uos.smsmsm.recycleradapter.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.ItemFindFriendAndAddFriendBinding
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.viewmodel.UserUtilViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FindFriendAdapter(val context: Context, val userUtilViewModel: UserUtilViewModel): RecyclerView.Adapter<FindFriendViewHolder>(){
    var findUserList = ArrayList<UserDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendViewHolder = FindFriendViewHolder(
        ItemFindFriendAndAddFriendBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    override fun onBindViewHolder(holder: FindFriendViewHolder, position: Int) {
        if(findUserList.isNotEmpty()) {
            holder.bind(findUserList[position],userUtilViewModel )
        }
    }

    override fun getItemCount(): Int = findUserList.size

    fun setItem(friendList: ObservableArrayList<UserDTO>) {
        this.findUserList = friendList
        notifyDataSetChanged()
    }
}

class FindFriendViewHolder(val binding: ItemFindFriendAndAddFriendBinding) : RecyclerView.ViewHolder(binding.root){

    val userRepository = UserRepository()
    val ioScope = CoroutineScope(Dispatchers.Main)
    private lateinit var item : UserDTO
    fun bind(item: UserDTO, userUtilViewModel: UserUtilViewModel){
        //프로필 이미지 출력
        this.item = item
        item.uid?.let {
            ioScope.launch {
                userRepository.getUserProfileImage(it).collect {

                    Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop())
                        .into(binding.itemFindFriendAndAddFriendProfileImg)
                }
            }
        }
        binding.itemFindFriendAndAddFriendTextTitle.text = item.userName
        binding.itemFindFriendAndAddFriendAddImg.setOnClickListener{
            userUtilViewModel.addFriend(item.uid!!)
        }
    }
    fun getItem() : UserDTO = item

}