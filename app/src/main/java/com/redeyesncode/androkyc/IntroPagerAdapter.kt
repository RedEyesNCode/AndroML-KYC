package com.redeyesncode.androkyc

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.redeyesncode.androkyc.fragments.IntroFragment

class IntroPagerAdapter(fm:FragmentManager):FragmentPagerAdapter(fm) {
    private val fragmentList = mutableListOf<IntroFragment>()
    fun addFragment(fragment: IntroFragment) {
        fragmentList.add(fragment)
    }

    override fun getCount(): Int {

        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        val fragment = fragmentList[position]
        if(position==0){
            return IntroFragment.newInstance("document","")
        }else if(position==1){
            return IntroFragment.newInstance("face","")
        }else{
            return IntroFragment.newInstance("upload","")
        }


    }
}