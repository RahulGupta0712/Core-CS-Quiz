package com.example.core_quiz.DataModel

import com.example.core_quiz.R

data class UserData(var name:String, var email:String, var password:String, var age:Int, var country:String, var profilePic:Int, var coins:Long){
    constructor():this("", "", "", 0, "", R.drawable.user ,0)
}
