package com.uos.smsmsm.fragment.tabmenu.other

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.activity.setting.SettingActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.databinding.FragmentOtherMenuBinding
import com.uos.smsmsm.ui.photo.PhotoViewActivity
import com.uos.smsmsm.util.dialog.LoadingDialog

class OtherMenuFragment : BaseFragment<FragmentOtherMenuBinding>(R.layout.fragment_other_menu) {

    private val auth = FirebaseAuth.getInstance()
    private var destinationUid : String ? = null

    private var profileImageUri : String ? = null

    companion object{
        var PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentothermenu = this

        //프라그먼트 재활용시 uid 받아오기
        destinationUid = arguments?.getString("destinationUid")

        // 설정
        binding.fragmentOtherMenuSettingButton.setOnClickListener {
            startActivity(Intent(binding.root.context, SettingActivity::class.java))
        }

        //유저 프로필 사진 가져오기
        loadingDialog.show()
        userViewModel.getUserProfile(auth.currentUser!!.uid.toString())
        userViewModel.profileImage.observe(viewLifecycleOwner, Observer {
            profileImageUri = it.toString()
            Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(binding.fragmentOtherMenuCircle) })

        //유저 닉네임 가져오기
        userViewModel.getUserName(auth.currentUser!!.uid.toString())
        userViewModel.userName.observe(viewLifecycleOwner, Observer { binding.fragmentOtherMenuTextviewProfileNickname.text = it.toString()
        loadingDialog.dismiss()})

    }

    fun openManagePage(view: View){
    }

    fun onClickProfilePhoto(view: View) {
        if(destinationUid != auth.currentUser!!.uid) {
            //본인이면 사진 교체
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.apply {
                type = "image/*"
                startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
            }
        }else {
            //본인이 아니면 사진 보기
            var intent = Intent(binding.root.context,PhotoViewActivity::class.java)
            intent.apply {
                putExtra("imageUrl",profileImageUri)
                startActivity(intent)
            }
        }
    }

    fun onClickProfileBar(view: View){
        var intent = Intent(binding.root.context,ProfileActivity::class.java)
        intent.apply {
            intent.putExtra("uid" , auth.currentUser?.uid)
            startActivity(intent)
        }}

    fun onClickSettingButton(view: View) {
        startActivity(Intent(binding.root.context, SettingActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){

            var progressDialog = LoadingDialog(binding.root.context)
            progressDialog!!.setTitle("프로필 이미지를 저장 중입니다. 잠시만 기다려주세요.")
            progressDialog!!.show()

            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
                userViewModel.getUserProfile(auth.currentUser!!.uid)
                progressDialog.dismiss()
            }
        }
    }
}
