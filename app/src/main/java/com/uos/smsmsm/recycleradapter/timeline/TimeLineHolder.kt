package com.uos.smsmsm.recycleradapter.timeline

import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemTimelinePostBinding
import com.uos.smsmsm.repository.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimeLineHolder(binding: ItemTimelinePostBinding) : BaseHolder<ItemTimelinePostBinding, TimeLineDTO>(binding) {


    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val contentRepository = ContentRepository()

    override fun bind(element: TimeLineDTO) {
        super.bind(element)
        binding.itemtimelinepost = element

        //테스트용 글 내용 삽입
        binding.itemTimelinePostTextviewExplain.text = element.content!!.explain

        //테스트용 좋아요 클릭
        binding.itemTimelineImagebuttonFavorite.setOnClickListener {
            favoriteEvent( element.contentId.toString() )
        }

        isFavorite(element.contentId.toString())
    }

    fun favoriteEvent(contentId : String) {
        mainScope.launch {
            contentRepository.favoriteEvent(contentId).collect {
                if (it) isFavorite(contentId)
            }
        }
    }

    fun isFavorite(contentId : String) {
        mainScope.launch {
            //로딩 시작 넣어주기
            contentRepository.isFavorite(contentId).collect {
                if (it) {
                    //채워진 하트
                    binding.itemTimelineImagebuttonFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_36)
                    updateFavoriteCount(contentId)
                    //로딩 끝내기 넣어주기
                } else {
                    //빈 하트
                    binding.itemTimelineImagebuttonFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_border_36)
                    updateFavoriteCount(contentId)
                    //로딩 끝내기 넣어주기
                }
            }
        }
    }

    //좋아요 갯수 업데이트
    fun updateFavoriteCount(contentId : String) {
        mainScope.launch {
            //로딩 시작 넣어주기
            contentRepository.getFavoriteCountByContentId(contentId)
                .collect {
                    binding.itemTimelinePostTextviewFavoriteCount.text =
                        "좋아요 " + it.toString() + "개"
                    //로딩 끝내기 넣어주기
                }
        }

    }
}