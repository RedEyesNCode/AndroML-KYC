package com.redeyesncode.androkyc.camera

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.redeyesncode.androkyc.Constant
import com.redeyesncode.androkyc.activity.VerificationActivity
import com.redeyesncode.androkyc.databinding.ActivityCameraDetectBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CameraDetectActivity : AppCompatActivity() ,SurfaceHolder.Callback{
    private lateinit var camera: Camera
    private lateinit var cameraPreview: SurfaceView
    private lateinit var boxOverlay: CustomBoxOverlayView

    private lateinit var binding:ActivityCameraDetectBinding
    private var isCameraInitialized: Boolean = false

    private val CAMERA_PERMISSION_REQUEST = 100
    private val pictureCallback = Camera.PictureCallback { data, _ ->
        val pictureFile = getOutputMediaFile()
        try {
            val options = BitmapFactory.Options()
            options.inMutable = true

            // Decode the captured image data into a Bitmap
            val capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)

            // Rotate the captured image to a vertical orientation
//            val rotationMatrix = Matrix()
//            rotationMatrix.postRotate(90f)
//            val rotatedBitmap = Bitmap.createBitmap(capturedBitmap, 0, 0, capturedBitmap.width, capturedBitmap.height, rotationMatrix, true)
//

            // Save the cropped Bitmap to the picture file
            val fos = FileOutputStream(pictureFile)
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos)
            fos.flush()
            fos.close()
            // Saving  only the original bitmap and cropped bitmap.

            startCrop(pictureFile!!.toUri())


        } catch (e: FileNotFoundException) {
            Log.d(Constant.ANDROID_KYC, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(Constant.ANDROID_KYC, "Error accessing file: ${e.message}")
        }
    }
    fun cropBitmapToCreditCard(bitmap: Bitmap): Bitmap {
        val overlayRect = Rect()
        cameraPreview.getDrawingRect(overlayRect)
        (cameraPreview.parent as ViewGroup).offsetDescendantRectToMyCoords(cameraPreview, overlayRect)

        val overlayLeft = overlayRect.left
        val overlayTop = overlayRect.top
        val overlayWidth = overlayRect.width()
        val overlayHeight = overlayRect.height()

        val overlayBitmap = Bitmap.createBitmap(bitmap, overlayLeft, overlayTop, overlayWidth, overlayHeight)

        return overlayBitmap
    }


    private fun getOutputMediaFile(): File? {
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Constant.ANDROID_KYC, "Failed to create directory")
                return null
            }
        }

        return File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
    }
    fun saveBitmapToFile(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp_Cropped")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Constant.ANDROID_KYC, "Failed to create directory")
                return
            }
        }
        val file = File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            // Open the camera
            camera = Camera.open()
            camera.setDisplayOrientation(90)
            camera.setPreviewDisplay(holder)
            isCameraInitialized = true
            // Start the camera preview
            camera.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle preview size changes, if needed
//        if (!isCameraInitialized) return
//
//        // Stop the camera preview
//        camera.stopPreview()
//
//        // Set the camera parameters
//        val parameters = camera.parameters
//        parameters.setPreviewSize(width, height)
//        camera.parameters = parameters
//
//        // Restart the camera preview
//        camera.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
// Release the camera resources
        camera.stopPreview()
        camera.release()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraDetectBinding.inflate(layoutInflater)
        if (!hasCameraPermission()) {
            requestCameraPermission()
        } else {
            initializeCamera()
        }
        binding.btnCapture.setOnClickListener {
            if (isCameraInitialized) {
                camera.takePicture(null, null, pictureCallback)
            }

        }


        setContentView(binding.root)
    }
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    private fun initializeCamera() {

        // Initialize cameraPreview and boxOverlay views
        cameraPreview = binding.cameraPreview
        setupBoxOverlay()

        binding.mainLayout.removeAllViews()
        binding.mainLayout.addView(cameraPreview)
        binding.mainLayout.addView(boxOverlay, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        // Set SurfaceHolder.Callback on cameraPreview
        cameraPreview.holder.addCallback(this)
    }

    private fun setupBoxOverlay() {
        val boxWidthMm = 60f
        val boxHeightMm = 60f


        // Get screen dimensions in pixels
        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels

        // Calculate box dimensions in pixels
        val boxWidthPx = (boxWidthMm / 25.4f * displayMetrics.xdpi).toInt()
        val boxHeightPx = (boxHeightMm / 25.4f * displayMetrics.ydpi).toInt()

        // Calculate box coordinates to place it at the center
        val boxLeft = (screenWidthPx - boxWidthPx) / 2f
        val boxTop = (screenHeightPx - boxHeightPx) / 2f
        val boxRight = boxLeft + boxWidthPx
        val boxBottom = boxTop + boxHeightPx

        boxOverlay = CustomBoxOverlayView(this,boxLeft, boxTop, boxRight, boxBottom)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera()
            } else {
                // Camera permission denied, handle accordingly
            }
        }
    }
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent

            val uriFilePath = result.getUriFilePath(this@CameraDetectActivity) // optional usage
            Log.i(Constant.ANDROID_KYC,uriFilePath.toString())
            Log.i(Constant.ANDROID_KYC,uriFilePath.toString())

            saveBitmapToFile(result.getBitmap(this@CameraDetectActivity)!!)

            val resultIntent = Intent(this@CameraDetectActivity,VerificationActivity::class.java)
            resultIntent.putExtra("uri_file_path", uriFilePath)
            startActivity(resultIntent)
            finish()
            // Move to Verification Activity Instead.



        } else {
            // An error occurred.
            val exception = result.error
            Log.i(Constant.ANDROID_KYC,exception.toString())
            Log.i(Constant.ANDROID_KYC,exception.toString())
        }
    }
    private fun startCrop(uri: Uri){
        val options = CropImageOptions()
        options.cropShape = CropImageView.CropShape.RECTANGLE_HORIZONTAL_ONLY
        options.guidelines = CropImageView.Guidelines.ON
        options.fixAspectRatio = true


        cropImage.launch(CropImageContractOptions(uri,
            options
        ))
//        val cropImage = Intent(this@CameraDetectActivity,CropImageActivity::class.java)
//        val bundle = Bundle()
//        cropImage.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE,bundle)
//        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE,uri)
//        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS,options)
//        startActivity(cropImage)


    }
}