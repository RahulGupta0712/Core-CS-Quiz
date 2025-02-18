package com.example.core_quiz.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core_quiz.DataModel.Category
import com.example.core_quiz.QuestionsActivity
import com.example.core_quiz.databinding.ItemViewRvCategoryBinding

class CategoryAdapter(private var datalist : ArrayList<Category>, var context : Context) :RecyclerView.Adapter<CategoryAdapter.MyViewHolder>(){

    inner class MyViewHolder(var binding:ItemViewRvCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemViewRvCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.categoryName.text = datalist[position].name
        holder.binding.categoryImage.setImageResource(datalist[position].image)

        holder.binding.root.setOnClickListener{
            // move to questions activity with this category name, so that we can show questions from this category
            val intent = Intent(context, QuestionsActivity::class.java)
            intent.putExtra("quiz subject image", datalist[position].image)
            intent.putExtra("quiz subject", datalist[position].name)
            context.startActivity(intent)
        }
    }
}