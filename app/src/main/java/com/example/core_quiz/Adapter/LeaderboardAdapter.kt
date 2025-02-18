package com.example.core_quiz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core_quiz.DataModel.LeaderboardData
import com.example.core_quiz.R
import com.example.core_quiz.databinding.ItemLeaderboardBinding
import com.google.firebase.auth.FirebaseAuth

class LeaderboardAdapter(private var datalist:ArrayList<LeaderboardData>, var context:Context) : RecyclerView.Adapter<LeaderboardAdapter.myViewHolder>(){
    inner class myViewHolder(var binding:ItemLeaderboardBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return myViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.binding.rank.text = "${position+1}."
        holder.binding.username.text = datalist[position].name
        holder.binding.noOfQuizes.text = datalist[position].countOfQuizes.toString()
        holder.binding.averageScore.text = round(datalist[position].averageScore)

        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser!!.uid == datalist[position].userId){
            holder.binding.root.setBackgroundResource(R.drawable.selected_leaderboard_item)
        }
        else{
            holder.binding.root.setBackgroundResource(R.drawable.design_item_leaderboard)
        }
    }

    private fun round(num:Double):String{
        return "%.2f".format(num)
    }
}