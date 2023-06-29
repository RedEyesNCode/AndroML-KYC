package com.redeyesncode.androkyc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.redeyesncode.androkyc.activity.PanInfoActivity
import com.redeyesncode.androkyc.databinding.ActivityIntroBinding
import com.redeyesncode.androkyc.fragments.IntroFragment

class IntroActivity : AppCompatActivity(),ViewPager.OnPageChangeListener {
    lateinit var binding:ActivityIntroBinding
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if(position==0){
            binding.btnGo.text = "NEXT"

        }else if(position==1){
            binding.btnGo.text = "NEXT"

        }else if(position==2){
            binding.btnGo.text = "Start KYC_PROCESS"
        }
    }

    override fun onPageSelected(position: Int) {
        if(position==0){
            binding.btnGo.text = "NEXT"

        }else if(position==1){
            binding.btnGo.text = "NEXT"

        }else if(position==2){
            binding.btnGo.text = "Start KYC_PROCESS"
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)

        val adapter = IntroPagerAdapter(supportFragmentManager)
        adapter.addFragment(IntroFragment.newInstance("document",""))
        adapter.addFragment(IntroFragment.newInstance("face",""))
        adapter.addFragment(IntroFragment.newInstance("upload",""))

        binding.viewPager.adapter = adapter


        binding.btnGo.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem

            if(currentPosition==2){
                val intent = Intent(this@IntroActivity,PanInfoActivity::class.java)
                startActivity(intent)
            }else{
                if (currentPosition < adapter.count - 1) {
                    binding.viewPager.currentItem = currentPosition + 1
                }
            }

        }
        setContentView(binding.root)
    }
}