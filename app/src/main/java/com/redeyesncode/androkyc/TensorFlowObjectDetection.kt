package com.redeyesncode.androkyc

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TensorFlowObjectDetection(private val assetManager:AssetManager) {
    lateinit var interpreter: Interpreter

    fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assetManager.openFd("object_detect_tensorflow.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun runInference(inputData: Any, outputData: Any) {
        interpreter.run(inputData, outputData)
    }

    fun initializeInterpreter() {
        val modelFile = loadModelFile()
        val options = Interpreter.Options()
        interpreter = Interpreter(modelFile, options)
    }
}