package com.example.core_quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.core_quiz.DataModel.UserData
import com.example.core_quiz.List.Countries
import com.example.core_quiz.databinding.ActivityProfileDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast

class ProfileDetails : AppCompatActivity() {
    private val binding by lazy {
        ActivityProfileDetailsBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var coins = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.emailEdit.setOnClickListener {
            FancyToast.makeText(this, "Email can't be edited!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
        }

        binding.passwordEdit.setOnClickListener {
            if(!binding.updatePasswordCheckButton.isChecked){
                FancyToast.makeText(this, "Select the Password Update Checkbox to update password!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }
        }

        // set up the countries spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.preference_category, Countries.list)
        binding.countryEdit.setAdapter(spinnerAdapter)
        binding.countryEdit.onClearClick {
            binding.countryEdit.clearSelectedText()
        }

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        binding.emailEdit.setText(auth.currentUser!!.email)

        // show previous data if exists
        val userId = auth.currentUser!!.uid
        databaseReference.child("users").child(userId).child("userData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // show data in edittext
                        val data = dataSnapshot.getValue(UserData::class.java)
                        data?.let {
                            binding.nameEdit.setText(data.name)
                            binding.passwordEdit.setText(data.password)
                            binding.ageEdit.setText(data.age.toString())
                            binding.countryEdit.setText(data.country)
                            coins = data.coins
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        binding.emailEdit.isEnabled = false
        binding.passwordEdit.isEnabled = false

        val googleSignedIn = intent.getBooleanExtra("google", false)
        if (googleSignedIn) {
            // don't show password as we don't have one for google signed-in users, also don't show update password button too
            binding.passwordLL.visibility = View.GONE
            binding.updatePasswordCheckButton.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val email = auth.currentUser!!.email!!
            var password = binding.passwordEdit.text.toString()
            val ageText = binding.ageEdit.text.toString()
            var age = 0
            if (ageText.isNotEmpty()) age = ageText.toInt()
            val country = binding.countryEdit.text.toString()

            if (name.isEmpty()) {
                FancyToast.makeText(
                    this, "Name can't be empty!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false
                ).show()
            } else if ((binding.updatePasswordCheckButton.isChecked) && (password.length < 10)) {
                FancyToast.makeText(
                    this,
                    "Password must have a length >= 10!",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.WARNING,
                    false
                ).show()
            } else if (age <= 0 || age > 125) {
                FancyToast.makeText(
                    this, "Invalid Age", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false
                ).show()
            } else if (country.uppercase() !in Countries.list) {
                FancyToast.makeText(
                    this,
                    "Country not in the list!",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.WARNING,
                    false
                ).show()
            } else {

                if (googleSignedIn) {
                    updateData(userId, name, email, password, age, country, R.drawable.user)
                } else if (binding.updatePasswordCheckButton.isChecked) {
                    auth.currentUser!!.updatePassword(password)
                    updateData(userId, name, email, password, age, country, R.drawable.user)
                } else if (!binding.updatePasswordCheckButton.isChecked) {
                    databaseReference.child("users").child(userId).child("userData")
                        .child("password").get().addOnSuccessListener {
                            password = it.value.toString()
                            updateData(userId, name, email, password, age, country, R.drawable.user)
                        }
                }
            }
        }

        binding.updatePasswordCheckButton.setOnCheckedChangeListener { _, isChecked ->
            binding.passwordEdit.isEnabled = isChecked
        }
    }

    private fun updateData(
        userId: String,
        name: String,
        email: String,
        password: String,
        age: Int,
        country: String,
        profilePic: Int
    ){
        databaseReference.child("users").child(userId).child("userData")
            .setValue(UserData(name, email, password, age, country, R.drawable.user,coins))
            .addOnSuccessListener {
                databaseReference.child("Leaderboard").child(auth.currentUser!!.uid).child("name").setValue(name)
                    .addOnSuccessListener {
                        FancyToast.makeText(
                            this,
                            "Data Updated Successfully!",
                            FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        FancyToast.makeText(
                            this, "Data Update Failed!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false
                        ).show()
                    }
            }.addOnFailureListener {
                FancyToast.makeText(
                    this, "Data Update Failed!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false
                ).show()
            }
    }
}