package com.example.core_quiz.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core_quiz.Adapter.LeaderboardAdapter
import com.example.core_quiz.DataModel.LeaderboardData
import com.example.core_quiz.databinding.FragmentLeaderboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentLeaderboard : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val binding by lazy {
        FragmentLeaderboardBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    private lateinit var datalist: ArrayList<LeaderboardData>
    private lateinit var adapter: LeaderboardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datalist = ArrayList()
        adapter = LeaderboardAdapter(datalist, requireActivity())

        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvLeaderboard.adapter = adapter


        val db = FirebaseDatabase.getInstance().reference

        db.child("Leaderboard").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist.clear()
                for (snap in snapshot.children) {
                    val data = snap.getValue(LeaderboardData::class.java)
                    data?.let {
                        datalist.add(data)
                    }
                }
                datalist.sortByDescending { it.averageScore }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }


    }
}