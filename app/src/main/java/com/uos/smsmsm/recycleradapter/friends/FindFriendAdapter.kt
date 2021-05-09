package com.uos.smsmsm.recycleradapter.friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.ItemFindFriendAndAddFriendBinding
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
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
    private var isAlreadyFriends : Boolean = false
    fun bind(item: UserDTO, userUtilViewModel: UserUtilViewModel){
        isAlreadyFriends = false
        //프로필 이미지 출력
        this.item = item
        item.uid?.let {
            ioScope.launch {
                userRepository.getUserProfileImage(it).collect {

                    Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop())
                        .into(binding.itemFindFriendAndAddFriendProfileImg)
                }
            }
            for( i in SNSUtilViewModel.friendsUidList){
                if(it == i){
                    isAlreadyFriends = true
                    break;
                }
            }
        }
        binding.itemFindFriendAndAddFriendTextTitle.text = item.userName
        binding.itemFindFriendAndAddFriendAddImg.setOnClickListener{
            if(!isAlreadyFriends) {
                userUtilViewModel.addFriend(item.uid!!)
            }else{
                Toast.makeText(binding.root.context, "이미 등록된 친구 입니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun getItem() : UserDTO = item

}