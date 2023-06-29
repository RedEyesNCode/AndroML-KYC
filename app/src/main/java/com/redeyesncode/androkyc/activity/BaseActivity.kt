package com.redeyesncode.androkyc.activity

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity:AppCompatActivity() {

    fun showMessageDialog(message:String,title:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
        }

        val mDialog = builder.create()
        if(!mDialog.isShowing){
            mDialog.show()

        }
//        Toast.makeText(this@BaseActivity,message,Toast.LENGTH_SHORT).show()


    }
}