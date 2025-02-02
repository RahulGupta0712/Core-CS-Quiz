package com.example.core_quiz.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.core_quiz.Fragments.FragmentHome
import com.example.core_quiz.Fragments.FragmentLeaderboard
import com.example.core_quiz.Fragments.FragmentProfile

class viewPagerAdapter(fragmentActivity:FragmentActivity) :FragmentStateAdapter(fragmentActivity){

    val fragments = arrayOf(FragmentHome(), FragmentLeaderboard(), FragmentProfile())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}