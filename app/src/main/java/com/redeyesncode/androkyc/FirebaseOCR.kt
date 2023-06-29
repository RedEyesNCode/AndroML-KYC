package com.redeyesncode.androkyc

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

open class FirebaseOCR(val context: Context, val firebaseOcr: Uri) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    var _visionTextResult:MutableLiveData<String> = MutableLiveData()
    var visionTextLive :LiveData<String> = _visionTextResult
   
    fun runMLKit(){
        try {
            val image = InputImage.fromFilePath(context, firebaseOcr)
            var message:String = ""
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...

                    Log.i(Constant.ANDROID_KYC,visionText.text.toString())
                    Log.i(Constant.ANDROID_KYC,visionText.textBlocks.toTypedArray().toString())
                    message = visionText.text.toString()
                    _visionTextResult.postValue(message)

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Log.i(Constant.ANDROID_KYC,e.toString())
                    Log.i(Constant.ANDROID_KYC,e.toString())
                    Log.i(Constant.ANDROID_KYC,e.toString())
                    Log.i(Constant.ANDROID_KYC,e.toString())
                    Log.i(Constant.ANDROID_KYC,e.toString())
                    message= "EXCEPTION"
                    _visionTextResult.postValue(message)


                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
            _visionTextResult.postValue("IO_EXCEPTION_ML_KIT")

        }

    }
}