package com.example.core_quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.core_quiz.Authentication.MainAuthActivity
import com.example.core_quiz.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val binding by lazy{
        ActivitySplashScreenBinding.inflate(layoutInflater)
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

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            val userId = user.uid
            databaseReference.child("users").child(userId).child("userData")
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Entry exists — [User Exists]
                            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                            finish()
                        }
                        else{
                            startActivity(Intent(this@SplashScreen, MainAuthActivity::class.java))
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        else{
            startActivity(Intent(this, MainAuthActivity::class.java))
            finish()
        }
    }
}