package com.example.core_quiz.DataModel

data class LeaderboardData(var name : String, var countOfQuizes:Int, var averageScore:Double, var sumOfScores:Long, var userId:String){
    constructor():this("", 0,0.0,0, "")
}
