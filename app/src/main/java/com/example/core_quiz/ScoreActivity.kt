package com.example.core_quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.core_quiz.DataModel.HistoryData
import com.example.core_quiz.DataModel.LeaderboardData
import com.example.core_quiz.databinding.ActivityScoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast

class ScoreActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScoreBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val score = intent.getIntExtra("score", 0)
        val subject = intent.getStringExtra("subject")?:""

        val db = FirebaseDatabase.getInstance().reference
        val auth = FirebaseAuth.getInstance()

        // add this in history
        db.child("users").child(auth.currentUser!!.uid).child("History").push().setValue(HistoryData(subject, System.currentTimeMillis(), score))

        binding.score.text = "Your Score : $score / 10"

        binding.message.text =  when(score){
            6 -> {
                "Great effort! You're on the right track. A little more practice, and you'll be hitting higher scores in no time! 💪"
            }
            7->{
                "Nice work! You're showing solid progress. Keep going, and you'll master these concepts soon! 🎯"
            }
            8->{
                "Awesome job! You're getting really close to perfection. Keep up the momentum! 🔥"
            }
            9->{
                "Fantastic! You have a strong grasp of the subject. Just one more step to a perfect score! 🌟"
            }
            10->{
                "Perfect score! You're a true CS quiz master! Keep up the amazing work! 🎉🚀"
            }
            else->{
                "Don't be discouraged! Every attempt is a step closer to improvement. Keep practicing, and you'll get better each time! 🚀"
            }
        }

        db.child("Leaderboard").child(auth.currentUser!!.uid).runTransaction(object: Transaction.Handler{
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val newData = currentData.getValue(LeaderboardData::class.java)?:return Transaction.success(currentData)
                newData.countOfQuizes++
                newData.sumOfScores += score
                newData.averageScore = newData.sumOfScores.toDouble() / newData.countOfQuizes
                currentData.value = newData
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if(error != null){
                    FancyToast.makeText(this@ScoreActivity, "Coins Update Failed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                }
            }

        })


        if (score >= 5) {
            // update coins

            // we will use transactions to retrieve coins value and then update it, as by naive method it moves into race condition and keeps calling each other again and again leading to infinite loop and infinite updates

            db.child("users").child(auth.currentUser!!.uid).child("userData").child("coins").runTransaction(object: Transaction.Handler{
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    // retrieve coins value currently
                    val coins = currentData.getValue(Long::class.java)?:0
                    currentData.value = coins + score * 10
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if(error != null){
                        FancyToast.makeText(this@ScoreActivity, "Coins Update Failed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                    }
                }

            })

        }

        binding.moveToHomeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}