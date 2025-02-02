package com.example.core_quiz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.core_quiz.Adapter.viewPagerAdapter
import com.example.core_quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
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

        val Adapter = viewPagerAdapter(this)
        binding.viewPager.adapter=Adapter

        binding.viewPager.isUserInputEnabled=true // allowing scrolling

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragmentHome -> binding.viewPager.currentItem = 0
                R.id.fragmentLeaderboard -> binding.viewPager.currentItem = 1
                R.id.fragmentProfile -> binding.viewPager.currentItem = 2
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object:OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                // when page changes, then update the selected item on bottom nav view
                binding.bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }
}