package com.example.core_quiz.CountdownTimer

import android.os.CountDownTimer
import android.util.Log
import com.example.core_quiz.QuestionsActivity
import com.example.core_quiz.databinding.ActivityQuestionsBinding

class Timer(var totalTime:Long, interval:Long, var binding:ActivityQuestionsBinding):CountDownTimer(totalTime, interval) {
    override fun onTick(timeLeft: Long) {
        binding.progressBarTimer.progress = (((totalTime -  timeLeft)*100)/totalTime).toInt()
    }

    override fun onFinish() {
        QuestionsActivity.timeUp = true
        binding.progressBarTimer.progress = 0
        binding.nextButton.performClick()
    }
}