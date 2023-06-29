package com.redeyesncode.androkyc

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.redeyesncode.androkyc.camera.CameraDetectActivity
import com.redeyesncode.androkyc.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val IMAGE_CAPTURE_REQUEST_CODE = 200
    private val IMAGE_CAPTURE_CROP_REQUEST_CODE = 400
    lateinit var binding:ActivityMainBinding
    var currentPhotoPath:String = ""
    lateinit var firebaseOCR: FirebaseOCR

    lateinit var tensorFlowObjectDetection: TensorFlowObjectDetection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initClicks()
        // pan card animate.
        Helper().panCardEditTextListener("ABCDE1234F",binding.editTextPanNumber)
//        tensorFlowOCR.initializeInterpreter()
        setContentView(binding.root)
    }
    private fun attachObservers(){
        firebaseOCR.visionTextLive.observe(this, Observer {
            run {
                if (validateOcrPanCard(it)) {
                    binding.chkImage.isChecked = true
                    binding.chkFailImage.isChecked = false

                } else {
                    binding.chkFailImage.isChecked = true
                    binding.chkImage.isChecked = false

                }
                if (Helper().validateEditTextPanCard(binding.editTextPanNumber.text.toString())) {
                    binding.chkPanNumber.isChecked = true
                    binding.chkFailPanNumber.isChecked = false

                } else {
                    binding.chkFailPanNumber.isChecked = true
                    binding.chkPanNumber.isChecked = false

                }




            }

        })
    }
    private fun initClicks() {
        binding.btnReset.setOnClickListener {
            binding.editTextPanNumber.setText("")
            currentPhotoPath = ""
            binding.chkImage.isChecked = false
            binding.chkPanNumber.isChecked = false
            binding.chkFailImage.isChecked = false
            binding.chkFailPanNumber.isChecked = false

        }

        binding.btnVerify.setOnClickListener {
            /*else if(!Helper().validateEditTextPanCard(binding.editTextPanNumber.text.toString())){
                binding.editTextPanNumber.error = "INVALID PAN CARD NUMBER"

            }*/
            if(binding.editTextPanNumber.text.toString().isEmpty()){
                binding.editTextPanNumber.error = "Please enter pan number"
            }else if(currentPhotoPath.isEmpty()){
                Toast.makeText(this@MainActivity,"Please click picture of pan card.",Toast.LENGTH_LONG).show()

            }else{
                try {

                    if(currentPhotoPath!=null){

                        firebaseOCR.runMLKit()
                    }
                }catch (e:Exception){
                   Log.i("E_KYC",e.message.toString())
                   Log.i("E_KYC",e.message.toString())
                   Log.i("E_KYC",e.message.toString())
                    Toast.makeText(this@MainActivity,e.message.toString(),Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }


        }

        binding.ivPanImage.setOnClickListener {
            if(Helper().isCameraPermissionGranted(this@MainActivity)){
                if(binding.editTextPanNumber.text.toString().isEmpty()){
                    binding.editTextPanNumber.error = "Please enter pan number first !"
                }else{
                    captureImage()
                }


            }else{
                Helper().requestCameraPermission(this@MainActivity,CAMERA_PERMISSION_REQUEST_CODE)

            }

        }
        binding.btnCameraCrop.setOnClickListener {
            if(binding.editTextPanNumber.text.toString().isEmpty()){
                binding.editTextPanNumber.error = "Please enter pan number first !"
            }else{
                val intent = Intent(this@MainActivity,CameraDetectActivity::class.java)

                startActivityForResult(intent,IMAGE_CAPTURE_CROP_REQUEST_CODE)
            }

        }

    }


    private fun captureImage() {
        val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Handle file creation error

            null

        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                it
            )
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            imageCaptureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(imageCaptureIntent, IMAGE_CAPTURE_REQUEST_CODE)
        }

        if(photoFile==null){
            Toast.makeText(this@MainActivity,"error!",Toast.LENGTH_LONG).show()

        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath

        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            } else {
                // Camera permission denied, handle accordingly
                Toast.makeText(this@MainActivity,"Denied Permission",Toast.LENGTH_LONG)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val capturedImageUri: Uri? = data?.data
            // Use the capturedImageUri as needed


            binding.ivPanImage.setImageURI(Uri.parse(currentPhotoPath))
            firebaseOCR = FirebaseOCR(this@MainActivity, Uri.fromFile(File(currentPhotoPath)))
            attachObservers()
            firebaseOCR.runMLKit()


            val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            // do not resize the bitmap.
            val colorsToCheck = intArrayOf(Color.rgb(139,178,230), Color.rgb(140,193,243), Color.rgb(215,198,208))

            if(validatePanBitmap(bitmap,colorsToCheck)){
                Toast.makeText(this@MainActivity,"BITMAP_VALIDATED",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@MainActivity,"INVALID_BITMAP",Toast.LENGTH_LONG).show()
            }

            val firebaseObjectDetection = FirebaseObjectDetection(this@MainActivity,Uri.fromFile(File(currentPhotoPath)))
            firebaseObjectDetection.runMLObjectKit()

//            Toast.makeText(this@MainActivity,"${capturedImageUri.toString()}",Toast.LENGTH_LONG)

        }else if(requestCode == IMAGE_CAPTURE_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val filePath = data?.getStringExtra("uri_file_path")
            binding.ivPanImage.setImageURI(Uri.parse(filePath))
            firebaseOCR = FirebaseOCR(this@MainActivity, Uri.fromFile(File(filePath)))
            attachObservers()
            firebaseOCR.runMLKit()

            val firebaseObjectDetection = FirebaseObjectDetection(this@MainActivity,Uri.fromFile(File(filePath)))
            firebaseObjectDetection.runMLObjectKit()


        }
    }

    // This function contains various checks on the images that we need to perform for the indian PAN CARD.
    // ONLY STRING CHECK WILL WORK HERE.
    private fun validateOcrPanCard(ocrResult: String):Boolean {
        if(!ocrResult.contains(binding.editTextPanNumber.text.toString())){
            return false
        }else return ocrResult.contains(binding.editTextPanHolderName.text.toString())


    }

    private fun validatePanBitmap(bitmap: Bitmap,colors:IntArray):Boolean{
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