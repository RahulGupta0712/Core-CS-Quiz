package com.example.core_quiz.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core_quiz.Adapter.HistoryAdapter
import com.example.core_quiz.Authentication.MainAuthActivity
import com.example.core_quiz.DataModel.HistoryData
import com.example.core_quiz.DataModel.UserData
import com.example.core_quiz.ProfileDetails
import com.example.core_quiz.R
import com.example.core_quiz.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast


class FragmentProfile : Fragment() {
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private var isGoogleSignedInUserOnly = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()

        var googleLogIn = false
        var emailLogIn = false
        for (provider in auth.currentUser!!.providerData) {
            if (provider.providerId == GoogleAuthProvider.PROVIDER_ID) {
                // signed in via google
                googleLogIn = true
            } else if (provider.providerId == EmailAuthProvider.PROVIDER_ID) {
                // signed in via email and password
                emailLogIn = true
            }
        }

        if (googleLogIn && !emailLogIn) {
            // don't show password as only google log in is done
            isGoogleSignedInUserOnly = true
        }

        return binding.root
    }

    var isExpandedNow = false
    var isExpandedHistoryNow = false

    private lateinit var datalist: ArrayList<HistoryData>
    private lateinit var adapter: HistoryAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.SignoutButton.setOnClickListener {
            auth.signOut()
            FancyToast.makeText(
                requireActivity(),
                "Sign out successful",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                false
            ).show()
            startActivity(Intent(requireActivity(), MainAuthActivity::class.java))
            while (requireActivity().supportFragmentManager.backStackEntryCount > 0)
                requireActivity().supportFragmentManager.popBackStack()
        }

        binding.expandButton.setOnClickListener {
            if (isExpandedNow) {
                binding.expandButton.setImageResource(R.drawable.arrow_down)
                binding.expandableCL.visibility = View.GONE
            } else {
                binding.expandButton.setImageResource(R.drawable.arrow_up)
                binding.expandableCL.visibility = View.VISIBLE
                if (isGoogleSignedInUserOnly) binding.passwordLL2.visibility = View.GONE
            }
            isExpandedNow = !isExpandedNow
        }


        datalist = ArrayList()
        adapter = HistoryAdapter(datalist)

        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter = adapter

        val db = FirebaseDatabase.getInstance().reference

        db.child("users").child(auth.currentUser!!.uid).child("History").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    datalist.clear()

                    for (snap in snapshot.children) {
                        val hist = snap.getValue(HistoryData::class.java)
                        hist?.let {
                            datalist.add(hist)
                        }
                    }

                    datalist.reverse()
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        binding.expandButtonHistory.setOnClickListener {
            if (isExpandedHistoryNow) {
                binding.expandButtonHistory.setImageResource(R.drawable.arrow_down)
                binding.frameLayout2.visibility = View.GONE
            } else {
                binding.expandButtonHistory.setImageResource(R.drawable.arrow_up)
                binding.frameLayout2.visibility = View.VISIBLE

                if (datalist.isEmpty()) {
                    // show empty message
                    binding.emptyMessageHistory.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.emptyMessageHistory.visibility = View.GONE
                }
            }
            isExpandedHistoryNow = !isExpandedHistoryNow
        }

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileDetails::class.java)
            if (isGoogleSignedInUserOnly) intent.putExtra("google", true)
            startActivity(intent)
        }

        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser!!.uid
        databaseReference.child("users").child(userId).child("userData")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(UserData::class.java)
                    data?.let {
                        binding.name.text = data.name
                        binding.profileName.text = data.name
                        binding.email.text = data.email
                        binding.password.text = data.password
                        binding.age.text = data.age.toString()
                        binding.country.text = data.country
                        try{
                            binding.profileImage.setImageResource(data.profilePic)
                        }
                        catch(e:Exception){
                            binding.profileImage.setImageResource(R.drawable.user)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}