package com.redeyesncode.androkyc.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.redeyesncode.androkyc.R
import com.redeyesncode.androkyc.databinding.ActivityFaceDetectBinding

class FaceDetectActivity : AppCompatActivity() {

    lateinit var binding:ActivityFaceDetectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}