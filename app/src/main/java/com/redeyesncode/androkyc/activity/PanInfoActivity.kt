package com.redeyesncode.androkyc.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.redeyesncode.androkyc.Constant
import com.redeyesncode.androkyc.Helper
import com.redeyesncode.androkyc.R
import com.redeyesncode.androkyc.User
import com.redeyesncode.androkyc.camera.CameraDetectActivity
import com.redeyesncode.androkyc.databinding.ActivityPanInfoBinding
import com.redeyesncode.androkyc.session.AppSession
import java.util.Calendar

class PanInfoActivity : BaseActivity() {
    lateinit var binding:ActivityPanInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPanInfoBinding.inflate(layoutInflater)
        initClicks()

        Helper().panCardEditTextListener("OAJPS2502G",binding.editTextPanNumber)



        setContentView(binding.root)
    }

    fun showDatePickerDialog(context: Context, editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                editText.setText(formattedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun initClicks() {
        binding.btnUploadPan.setOnClickListener {
            if(isValidate()){
                val cameraPreviewIntent = Intent(this@PanInfoActivity,CameraDetectActivity::class.java)
                val user = User(binding.editTextPanHolderName.text.toString(),binding.editTextPanNumber.text.toString(),binding.editTextDateOfBirth.text.toString())
                AppSession(this@PanInfoActivity).putObject(Constant.KYC_USER,user)
                startActivity(cameraPreviewIntent)
            }

        }
        binding.editTextDateOfBirth.setOnClickListener {
            showDatePickerDialog(this@PanInfoActivity,binding.editTextDateOfBirth)
        }


    }
    private fun isValidate():Boolean{
        if(binding.editTextPanHolderName.text.toString().isEmpty()){
            binding.editTextPanHolderName.error = "Please Enter PAN Name"
            return false
        }else if(binding.editTextPanNumber.text.toString().isEmpty()){
            binding.editTextPanNumber.error = "Please Enter PAN Number"
            return false
        }else if(binding.editTextDateOfBirth.text.toString().isEmpty()){
            showMessageDialog("Please select Date of Birth","Alert !")
            return false
        }else{
            return true
        }
    }
}