package com.example.goodcall

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.Exception

class ViewPagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa) {
    //Number of fragments the adapter uses
    override fun getItemCount(): Int {
        return 3
    }

    //Shows which fragment to create depending on which page the user is on:
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FragmentSlide1()
            1 -> FragmentSlide2()
            2 -> FragmentSlide3()
            else -> throw Exception()
        }
    }
}