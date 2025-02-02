package com.example.core_quiz.DataModel

import com.example.core_quiz.R

data class Category(var image:Int, var name:String){
    constructor():this(R.drawable.background_transparent, "")
}
