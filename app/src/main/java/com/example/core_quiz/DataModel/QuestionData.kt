package com.example.core_quiz.DataModel

data class QuestionData(var question:String, var option1:String, var option2:String, var option3:String, var option4:String, var correctAns:String, var randomIndex:Double){
    constructor():this("", "", "", "", "", "", 0.0)
    // randomIndex is used to sort the data in firestore database which stores question, so that we can have some random questions
}
