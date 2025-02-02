package com.example.core_quiz.DataModel

data class HistoryData(var category:String, var time:Long, var score:Int){
    constructor():this("", 0, 0)
}
