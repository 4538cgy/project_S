package com.uos.smsmsm.recycleradapter.friends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ItemMultiViewFriendsListTypeTitleBinding
import com.uos.smsmsm.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
//친구 리스트에서 검색하는 결과 adapter
class FriendListSearchAdapter(val context : Context)  : RecyclerView.Adapter<FriendListSearchHolder>(){
    private var list = ArrayList<RecyclerDefaultModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListSearchHolder
    = FriendListSearchHolder(ItemMultiViewFriendsListTypeTitleBinding.inflate(LayoutInflater.from(context),parent, false))

    override fun onBindViewHolder(holder: FriendListSearchHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int  = position
    fun setItem(list : ArrayList<RecyclerDefaultModel>){
        this.list = list
        notifyDataSetChanged()
    }

}
class FriendListSearchHolder(val binding : ItemMultiViewFriendsListTypeTitleBinding): RecyclerView.ViewHolder(binding.root){

    val userRepository = UserRepository()
    val ioScope = CoroutineScope(Dispatchers.Main)
    private lateinit var item : RecyclerDefaultModel
    fun bind(item : RecyclerDefaultModel){
        this.item = item
        item.uid?.let {
            ioScope.launch {
                userRepository.getUserProfileImage(it).collect {

                    Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop())
                        .into(binding.itemMultiViewFriendsListTypeTitleImageview)
                }
            }
        }
        binding.itemMultiViewFriendsListTypeTitleTextviewTitle.text = item.title
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, ProfileActivity::class.java).apply {
                putExtra("uid", item.uid)
            }
            binding.root.context.startActivity(intent)
        }
    }
}