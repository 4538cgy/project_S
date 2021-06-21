package com.uos.smsmsm.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.friendfind.MyQrCodeActivity
import com.uos.smsmsm.activity.report.ReportActivity
import com.uos.smsmsm.activity.timeline.TimeLineActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.databinding.FragmentProfileBinding
import com.uos.smsmsm.fragment.tabmenu.friendslist.FriendsListFragment
import com.uos.smsmsm.ui.photo.PhotoViewActivity
import com.uos.smsmsm.util.workmanager.SubscribeWorker
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import java.util.Observer

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val auth = FirebaseAuth.getInstance()

    var destinationUid: String? = null
    private var destinationUserProfileUrl: String? = null

    //프로필 편집 버튼을 눌렀을때 뷰 변화 기준
    private var updateOnOff = false
    private var isFavorite = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragmentprofile = this@ProfileFragment
        }
        //액티비티로부터 destinationUid 가져오기
        destinationUid = requireArguments().getString("uid")

        //유저 닉네임 가져오기
        userViewModel.getUserName(destinationUid.toString())
        userViewModel.userName.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
             binding.fragmentProfileTextviewNickname.text = it
        })

        //유저 프로필 사진 가져오기
        userViewModel.getUserProfile(destinationUid.toString())
        userViewModel.profileImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            destinationUserProfileUrl = it.toString()

            Glide.with(rootContext)
                .load(it.toString())
                .circleCrop()
                .into(binding.fragmentProfileImageviewProfile)


        })


        // 친구 즐겨찾기 변경여부 observe
        userViewModel.isFavoriteResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                setFavoriteImg(it.isFavorite)
            } ?: {
                Toast.makeText(
                    this.context,
                    getString(R.string.fail_to_request_favorite_to_friend),
                    Toast.LENGTH_LONG
                ).show()
            }()
        })
        //본인인지 아닌지 구분
        isMe(destinationUid.toString())

        // 친구 리스트 중에 uid 비교해서 즐겨찾기 여부 체크
        for (i in SNSUtilViewModel.friendsList) {
            if (i.uid == destinationUid) {
                setFavoriteImg(i.isFavorite)
                break
            }
        }

        initDestinationUserData()

        userViewModel.currentDestinationUser.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
               binding.fragmentProfileTextviewNickname.text = it.userName


                Glide.with(rootContext)
                    .load("https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/userProfileImages%2F3Mxl3osZxGW3eghWwy8FJFVEEPt2?alt=media&token=19080e5e-6cca-4f1e-a056-0e59740c3d43")
                    .apply(
                        RequestOptions().centerCrop().circleCrop()
                    )
                    .into(binding.fragmentProfileImageviewProfile)


            }
        )


        //친구 추가가 성공했는지 지켜보기
        userViewModel.isSuccessAddFirends.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            loadingDialogText.show()
            loadingDialogText.run{
                when (it) {
                    "SUBSCRIBE_CREATE" -> {
                        binding.dialogProgressLoadingTextTextview.text = "친구 추가성공"
                        dismiss()
                        isFriend(true)
                        onSubscribeWorker()
                    }
                    "SUBSCRIBER_CREATE" -> {
                        binding.dialogProgressLoadingTextTextview.text = "사진이 호수에 도착!"
                        dismiss()
                        isFriend(true)
                        onSubscribeWorker()
                    }
                    "SUBSCRIBE_UPDATE" -> {
                        binding.dialogProgressLoadingTextTextview.text = "적은 글을 고이접어 우체통에 넣는중~"
                        dismiss()
                        isFriend(true)
                        onSubscribeWorker()
                    }
                    "SUBSCRIBER_UPDATE" -> {
                        binding.dialogProgressLoadingTextTextview.text = "배달완료!"
                        dismiss()
                        isFriend(true)
                        onSubscribeWorker()
                    }
                    else->{
                        binding.dialogProgressLoadingTextTextview.text = "친구 추가를 요청하는중"
                    }
                }
            }
        })


    }

    // 즐겨찾기 이미지 변경
    private fun setFavoriteImg(isFavorite: Boolean) {
        this.isFavorite = isFavorite
        if (isFavorite) {
            binding.fragmentProfileImagebuttonFavorite.setImageResource(R.drawable.ic_baseline_star_rate_24)
        } else {
            binding.fragmentProfileImagebuttonFavorite.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
    }

    fun onSubscribeWorker() {
        println("백그라운드 실행")
        var data: MutableMap<String, Any> = HashMap()

        data.put("WORK_STATE", SubscribeWorker.WORK_COPY_PASTE_CONTENTS)
        data.put("WORK_DESTINATION_UID", destinationUid.toString())

        val inputData = Data.Builder().putAll(data).build()

        val uploadManager: WorkRequest =
            OneTimeWorkRequestBuilder<SubscribeWorker>().setInputData(inputData).build()
        WorkManager.getInstance(rootContext).enqueue(uploadManager)
    }

    fun onClickTimeLine(view: View) {
        var intent = Intent(rootContext, TimeLineActivity::class.java)
        intent.apply {
            putExtra("destinationUid", destinationUid)
            startActivity(intent)
        }
    }

    fun showOptionPopup(view: View) {
        PopupMenu(rootContext, view).apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popup_profile_option_report -> {
                        var intent = Intent(rootContext, ReportActivity::class.java)
                        intent.apply {
                            putExtra("destinationUid", destinationUid)
                            startActivity(intent)
                        }

                    }
                }
                false
            }
            inflate(R.menu.popup_profile_option)
            show()
        }
    }

    fun showMyQrActivity(view: View) {
        startActivity(
            Intent(rootContext, MyQrCodeActivity::class.java).putExtra(
                "uid",
                "3VRMVIEfxfM0mK40Jr3j3ikmhAJ2"
            )
        )//auth.currentUser?.uid))
    }

    // 프로필 화면 닫기
    fun closeActivity(view: View) {
        //finish() 대신
        //이전 프라그먼트 보여주기
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.activity_lobby_fragmelayout,FriendsListFragment())
            .addToBackStack(null)
            .commit()
    }

    fun onClickUpdateProfile(view: View) {
        if (!updateOnOff) {

        binding.fragmentProfileLine1.visibility = View.VISIBLE
        binding.fragmentProfileLine2.visibility = View.VISIBLE
        binding.fragmentProfileLine3.visibility = View.VISIBLE
        binding.fragmentProfileImageviewProfileImageUpdate.visibility = View.VISIBLE
        binding.fragmentProfileImageviewEdit1.visibility = View.VISIBLE
        binding.fragmentProfileImageviewEdit2.visibility = View.VISIBLE
        binding.fragmentProfileUpdateBack.visibility = View.VISIBLE
        binding.fragmentProfileConstBottomBarIsmeLayout.visibility = View.GONE
        binding.fragmentProfileImagebuttonFavorite.visibility = View.GONE
        binding.fragmentProfileImagebuttonOption.visibility = View.GONE

            updateOnOff = true
        } else {


        binding.fragmentProfileLine1.visibility = View.GONE
        binding.fragmentProfileLine2.visibility = View.GONE
        binding.fragmentProfileLine3.visibility = View.GONE
        binding.fragmentProfileImageviewProfileImageUpdate.visibility = View.GONE
        binding.fragmentProfileImageviewEdit1.visibility = View.GONE
        binding.fragmentProfileImageviewEdit2.visibility = View.GONE
        binding.fragmentProfileUpdateBack.visibility = View.GONE
        binding.fragmentProfileConstBottomBarIsmeLayout.visibility = View.VISIBLE
        binding.fragmentProfileImagebuttonFavorite.visibility = View.VISIBLE
        binding.fragmentProfileImagebuttonOption.visibility = View.VISIBLE


            updateOnOff = false
        }
    }

    fun onClickProfileImage(view: View) {
        var intent = Intent(rootContext, PhotoViewActivity::class.java)
        intent.apply {
            putExtra("imageUrl", destinationUserProfileUrl)
            startActivity(intent)
        }
    }


    fun initDestinationUserData() {
        userViewModel.initDestinationUser(destinationUid.toString())
    }

    //친구 추가 버튼
    fun addFriend(view: View) {
        userViewModel.addFriend(destinationUid.toString())
    }

    fun isMe(uid: String) {
        if (uid == auth.currentUser?.uid) {


        binding.fragmentProfileConstBottomBarIsmeLayout.visibility = View.VISIBLE
        binding.fragmentProfileImagebuttonQr.visibility = View.VISIBLE
        binding.fragmentProfileConstBottomBarIsfriendLayout.visibility = View.GONE
        binding.fragmentProfileConstBottomBarIsnotfriendLayout.visibility = View.GONE
        binding.fragmentProfileImagebuttonFavorite.visibility = View.GONE


        } else {
            binding.fragmentProfileConstBottomBarIsmeLayout.visibility = View.GONE
            //친구인지 아닌지 구분
            userViewModel.checkFriend(destinationUid.toString())
            userViewModel.checkFriends.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                isFriend(it)
            })
        }
    }

    fun isFriend(boolean: Boolean) {

        if (boolean) {
            binding.fragmentProfileConstBottomBarIsfriendLayout.visibility = View.VISIBLE
            binding.fragmentProfileConstBottomBarIsnotfriendLayout.visibility = View.GONE

        } else {
            // 친구가 아닐 경우 즐겨찾기 아이콘 안나타나도록
            binding.fragmentProfileImagebuttonFavorite.visibility = View.GONE
            binding.fragmentProfileConstBottomBarIsfriendLayout.visibility = View.GONE
            binding.fragmentProfileConstBottomBarIsnotfriendLayout.visibility = View.VISIBLE
        }
    }

    // 친구 즐겨찾기 요청
    fun requestFavorite(view: View) {

        userViewModel.requestSetFavorite(destinationUid!!, !isFavorite)
    }

    fun openChat(view: View) {

        var intent = Intent(rootContext, ChatActivity::class.java)
        intent.apply {
            putExtra("destinationUid", destinationUid)

            startActivity(intent)
        }
    }


}