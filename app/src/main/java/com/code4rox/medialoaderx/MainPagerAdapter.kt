package com.code4rox.medialoaderx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val mFragments: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }

    override fun getItemCount(): Int = mFragments.size

    override fun createFragment(position: Int): Fragment = mFragments.get(position)
}