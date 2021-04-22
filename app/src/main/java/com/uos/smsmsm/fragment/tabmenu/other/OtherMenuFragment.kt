package com.uos.smsmsm.fragment.tabmenu.other

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import com.uos.smsmsm.databinding.FragmentOtherMenuBinding
import com.uos.smsmsm.testactivity.AddTestUser
import com.uos.smsmsm.ui.photo.PhotoViewActivity
import com.uos.smsmsm.util.dialog.LoadingDialog
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel

class OtherMenuFragment : Fragment() {

    lateinit var binding: FragmentOtherMenuBinding

    private val viewmodel: UserUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    lateinit var loadingDialog: LoadingDialog
    private var destinationUid : String ? = null

    private var profileImageUri : String ? = null

    companion object{
        var PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // View? -> View (NonNull)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_menu, container, false)
        binding.fragmentothermenu = this

        //프라그먼트 재활용시 uid 받아오기
        destinationUid = arguments?.getString("destinationUid")

        //프로그레스 초기화
        loadingDialog = LoadingDialog(binding.root.context)
        //프로그레스 투명하게
        loadingDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        loadingDialog!!.setCancelable(false)

        // 설정
        binding.fragmentOtherMenuSettingButton.setOnClickListener {
            startActivity(Intent(binding.root.context, SettingActivity::class.java))
        }

        //유저 프로필 사진 가져오기
        loadingDialog.show()
        viewmodel.getUserProfile(auth.currentUser!!.uid.toString())
        viewmodel.profileImage.observe(viewLifecycleOwner, Observer {
            profileImageUri = it.toString()
            Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(binding.fragmentOtherMenuCircle) })

        //유저 닉네임 가져오기
        viewmodel.getUserName(auth.currentUser!!.uid.toString())
        viewmodel.userName.observe(viewLifecycleOwner, Observer { binding.fragmentOtherMenuTextviewProfileNickname.text = it.toString()
        loadingDialog.dismiss()})


        return binding.root
    }

    fun openManagePage(view: View){
        startActivity(Intent(binding.root.context,AddTestUser::class.java))
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
                putExtra("photoUrl",profileImageUri)
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
        println("리퀘스트 code $requestCode // 리저트 code $resultCode")
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
                viewmodel.getUserProfile(auth.currentUser!!.uid)
                progressDialog.dismiss()
            }
        }
    }
}
