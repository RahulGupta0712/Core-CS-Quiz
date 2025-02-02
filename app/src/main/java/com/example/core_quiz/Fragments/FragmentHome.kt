package com.example.core_quiz.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.core_quiz.Adapter.CategoryAdapter
import com.example.core_quiz.DataModel.Category
import com.example.core_quiz.DataModel.UserData
import com.example.core_quiz.R
import com.example.core_quiz.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentHome : Fragment() {
    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var datalist: ArrayList<Category>
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        datalist = ArrayList()

        datalist.add(Category(R.drawable.os, "Operating Systems"))
        datalist.add(Category(R.drawable.cn, "Computer Networks"))
        datalist.add(Category(R.drawable.oops, "Object Oriented Programming"))
        datalist.add(Category(R.drawable.dbms, "Database Management Systems"))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCategories.layoutManager = GridLayoutManager(requireActivity(), 2)
        adapter = CategoryAdapter(datalist, requireActivity())
        binding.rvCategories.adapter = adapter

        val auth = FirebaseAuth.getInstance()
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser!!.uid

        if(auth.currentUser!!.email == FragmentAddQuestion().ADMIN){
            // add question feature is ON
            binding.addQuestionButton.visibility = View.VISIBLE
        }

        binding.addQuestionButton.setOnClickListener {
            val frag = FragmentAddQuestion()
            frag.show(requireActivity().supportFragmentManager, "add question")
        }

        databaseReference.child("users").child(userId).child("userData")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(UserData::class.java)
                    data?.let {
                        binding.userName.text = data.name
                        binding.userImage.setImageResource(data.profilePic)
                        binding.coins.text = "${data.coins} Coins"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}