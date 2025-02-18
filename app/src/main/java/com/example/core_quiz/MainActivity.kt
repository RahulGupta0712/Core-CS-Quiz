package com.example.core_quiz

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.core_quiz.Adapter.viewPagerAdapter
import com.example.core_quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var fragmentBackStack: ArrayDeque<Int> // will be used to store fragment ids
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fragmentBackStack = ArrayDeque()

        val Adapter = viewPagerAdapter(this)
        binding.viewPager.adapter = Adapter

        binding.viewPager.isUserInputEnabled = true // allowing scrolling

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragmentHome -> {
                    binding.viewPager.currentItem = 0
                }
                R.id.fragmentLeaderboard -> {
                    binding.viewPager.currentItem = 1
                }
                R.id.fragmentProfile -> {
                    binding.viewPager.currentItem = 2
                }
            }

            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // when page changes, then update the selected item on bottom nav view
                fragmentBackStack.addLast(position)
                binding.bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check whether the key event is the Back button and if there's history.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // go back to home fragment on a back click
            fragmentBackStack.removeLastOrNull() // removing current fragment from backstack
            val prevFragmentId = fragmentBackStack.removeLastOrNull() // removing the previous fragment too as the previous fragment will be automatically added by page change listener [Note that internal details of fragment wouldn't be stored, it will start that fragment freshly]
            if (prevFragmentId != null) {
                // currently not on home, so go to home fragment
                binding.viewPager.currentItem = prevFragmentId
                return true
            }
        }
        // If it isn't the Back button or there isn't web page history, bubble up to
        // the default system behavior. Probably exit the activity.
        return super.onKeyDown(keyCode, event)
    }
}