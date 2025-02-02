package com.example.core_quiz.Authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.core_quiz.R
import com.example.core_quiz.databinding.ActivityEmailVerifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast

class EmailVerifyActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEmailVerifyBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private var emailSendingFirstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser // the user who has signed up just now is currently present here

        if (emailSendingFirstTime)
            user!!.sendEmailVerification()
                .addOnSuccessListener {
                    FancyToast.makeText(
                        this,
                        "Email sent to ${user.email}",
                        FancyToast.LENGTH_LONG,
                        FancyToast.INFO,
                        false
                    ).show()
                    emailSendingFirstTime = false
                }

        binding.signInButton2.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}