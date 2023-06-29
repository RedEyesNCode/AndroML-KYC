package com.redeyesncode.androkyc

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.io.IOException

open class FirebaseObjectDetection(val context:Context,val imageUri:Uri) {
    val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableClassification()  // Optional
        .build()
    val objectDetector = ObjectDetection.getClient(options)
    fun runMLObjectKit(){
            val image = InputImage.fromFilePath(context, imageUri)
        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                for (detectedObject in detectedObjects) {
                    val boundingBox = detectedObject.boundingBox
                    val trackingId = detectedObject.trackingId
                    for (label in detectedObject.labels) {
                        val text = label.text
                        Log.i(Constant.ANDROID_KYC,text)
                        Log.i(Constant.ANDROID_KYC,text)
                        Log.i(Constant.ANDROID_KYC,text)
                        Log.i(Constant.ANDROID_KYC,boundingBox.toString())
                        Log.i(Constant.ANDROID_KYC,boundingBox.toString())
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.i(Constant.ANDROID_KYC,e.message.toString())
                Log.i(Constant.ANDROID_KYC,e.message.toString())

            }


    }


}