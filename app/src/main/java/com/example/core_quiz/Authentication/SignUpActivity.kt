package com.example.core_quiz.Authentication

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.core_quiz.DataModel.LeaderboardData
import com.example.core_quiz.DataModel.UserData
import com.example.core_quiz.MainActivity
import com.example.core_quiz.R
import com.example.core_quiz.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.shashank.sony.fancytoastlib.FancyToast

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
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

        // showing/hiding password in editText
        binding.showPasswordButton.setOnClickListener {
            // Toggle the password visibility based on the current state
            if (binding.passwordEditText.transformationMethod is PasswordTransformationMethod) {
                // Password is currently hidden, make it visible
                binding.passwordEditText.transformationMethod = null
                binding.showPasswordButton.setImageResource(R.drawable.hide_password) // Replace with your hide password icon
            } else {
                // Password is currently visible, make it hidden
                binding.passwordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.showPasswordButton.setImageResource(R.drawable.show_password) // Replace with your show password icon
            }

            // Force the EditText to redraw to reflect the change
            binding.passwordEditText.setSelection(binding.passwordEditText.length())
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                FancyToast.makeText(
                    this,
                    "Incomplete credentials",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.WARNING,
                    false
                ).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        // save details in database
                        val databaseReference = FirebaseDatabase.getInstance().reference
                        val userId = auth.currentUser!!.uid

                        databaseReference.child("users").child(userId).child("userData")
                            .setValue(UserData("", email, password, 0, "", R.drawable.user, 0))
                            .addOnSuccessListener {
                                FancyToast.makeText(
                                    this,
                                    "Sign Up successful",
                                    FancyToast.LENGTH_SHORT,
                                    FancyToast.SUCCESS,
                                    false
                                ).show()
                                databaseReference.child("Leaderboard").child(userId)
                                    .setValue(LeaderboardData("", 0, 0.0, 0, auth.currentUser!!.uid))
                                    .addOnSuccessListener {
                                        startActivity(Intent(this, EmailVerifyActivity::class.java))
                                        finish()
                                    }

                            }
                            .addOnFailureListener { e ->
                                FancyToast.makeText(
                                    this,
                                    "Error : ${e.message}",
                                    FancyToast.LENGTH_LONG,
                                    FancyToast.ERROR,
                                    false
                                ).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        FancyToast.makeText(
                            this,
                            "Error : ${e.message}",
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
            }
        }

        binding.signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, MainAuthActivity::class.java))
            finishAffinity()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}