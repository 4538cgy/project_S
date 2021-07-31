package com.uos.smsmsm.recycleradapter.customersupport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.R
import com.uos.smsmsm.data.FaqDTO
import com.uos.smsmsm.data.PropensityAnalysisDTO
import com.uos.smsmsm.databinding.ItemPropensityAnalysitsBinding
import com.uos.smsmsm.util.Delegate
import com.uos.smsmsm.viewmodel.AppUtilViewModel

class PropensityAnalysisAdapter (val context : Context, val appUtilViewModel : AppUtilViewModel) : RecyclerView.Adapter<PropensityAnalysisHolder>(){

    private var list : ArrayList<PropensityAnalysisDTO> = ArrayList<PropensityAnalysisDTO>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropensityAnalysisHolder
     = PropensityAnalysisHolder(ItemPropensityAnalysitsBinding.inflate(LayoutInflater.from(context), parent, false),appUtilViewModel)

    override fun onBindViewHolder(holder: PropensityAnalysisHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setItem(list : ArrayList<PropensityAnalysisDTO>){
        this.list = list
        notifyDataSetChanged()
    }
}

class PropensityAnalysisHolder(val binding : ItemPropensityAnalysitsBinding, val appUtilViewModel : AppUtilViewModel ) : RecyclerView.ViewHolder(binding.root){
    private var currentPosition : Int  = 0
    fun bind(item : PropensityAnalysisDTO){
        currentPosition = item.number
        binding.question = item.question
        binding.radioVeryNo.setOnClickListener {
            if(it is RadioButton && it.isChecked){
                appUtilViewModel.applyNumber(currentPosition, 1)
            }
        }
        binding.radioNo.setOnClickListener {
            if(it is RadioButton && it.isChecked){
                appUtilViewModel.applyNumber(currentPosition, 2)
            }
        }
        binding.radioSoso.setOnClickListener {
            if(it is RadioButton && it.isChecked){
                appUtilViewModel.applyNumber(currentPosition, 3)
            }
        }
        binding.radioYes.setOnClickListener {
            if(it is RadioButton && it.isChecked){
                appUtilViewModel.applyNumber(currentPosition, 4)
            }
        }
        binding.radioVeryYes.setOnClickListener {
            if(it is RadioButton && it.isChecked){
                appUtilViewModel.applyNumber(currentPosition, 5)
            }
        }

    }


}