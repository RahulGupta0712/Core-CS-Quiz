package com.example.core_quiz.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core_quiz.DataModel.HistoryData
import com.example.core_quiz.databinding.ItemViewHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date

class HistoryAdapter(private var datalist: ArrayList<HistoryData>) :
    RecyclerView.Adapter<HistoryAdapter.myViewHolder>() {
    inner class myViewHolder(var binding: ItemViewHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding =
            ItemViewHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return myViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.binding.scoreQuiz.text = "${datalist[position].score}/10"
        holder.binding.categoryQuiz.text = datalist[position].category

        val sc = datalist[position].score
        if(sc >= 5)
            holder.binding.coinsQuiz.text = "+${sc*10} "

        val time = Date(datalist[position].time) // Convert seconds to milliseconds
        val DateFormat = SimpleDateFormat("HH:mm, d MMMM, yyyy")
        val date = DateFormat.format(time)

        holder.binding.dateTimeQuiz.text = date
    }





}