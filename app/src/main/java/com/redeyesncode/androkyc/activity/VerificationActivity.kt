package com.redeyesncode.androkyc.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.redeyesncode.androkyc.Constant
import com.redeyesncode.androkyc.FirebaseOCR
import com.redeyesncode.androkyc.Helper
import com.redeyesncode.androkyc.R
import com.redeyesncode.androkyc.User
import com.redeyesncode.androkyc.databinding.ActivityVerificationBinding
import com.redeyesncode.androkyc.session.AppSession
import java.io.File

class VerificationActivity : AppCompatActivity() {
    lateinit var binding:ActivityVerificationBinding
    lateinit var firebaseOCR: FirebaseOCR



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val filePath = intent?.getStringExtra("uri_file_path")

        firebaseOCR = FirebaseOCR(this@VerificationActivity, Uri.fromFile(File(filePath)))
        attachObservers()
        firebaseOCR.runMLKit()

        binding.btnFaceVerification.setOnClickListener {
            startActivity(Intent(this@VerificationActivity,FaceDetectActivity::class.java))


        }

        setContentView(binding.root)
    }
    private fun attachObservers(){
        firebaseOCR.visionTextLive.observe(this, Observer {
            run {
                val filePath = intent?.getStringExtra("uri_file_path")

                val bitmap: Bitmap = BitmapFactory.decodeFile(File(filePath!!).absolutePath)
                // do not resize the bitmap.
                val colorsToCheck = intArrayOf(Color.rgb(139,178,230), Color.rgb(140,193,243), Color.rgb(215,198,208))

                if(validateOcrPanCard(it) && validatePanBitmap(bitmap,colorsToCheck)){
                    binding.lottieCheck.setAnimation(R.raw.lottie_check)
                    binding.lottieCheck.playAnimation()
                    binding.tvStatus.text = "Verification Success"
                }else{
                    binding.lottieCheck.setAnimation(R.raw.lottie_fail)
                    binding.lottieCheck.playAnimation()
                    binding.tvStatus.text = "Verification Failed"
                }




            }

        })
    }
    private fun validateOcrPanCard(ocrResult: String):Boolean {
        val userData = AppSession(this@VerificationActivity).getObject(Constant.KYC_USER,User::class.java) as User

        if(!ocrResult.contains(userData.panNumber)){
            return false
        }else return ocrResult.contains(userData.panName)


    }

    private fun validatePanBitmap(bitmap: Bitmap, colors:IntArray):Boolean{
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in pixels) {
            val color = Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
            if (colors.contains(color)) {
                return true
            }
        }

        return false
    }

}