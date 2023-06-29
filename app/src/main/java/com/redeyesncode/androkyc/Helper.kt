package com.redeyesncode.androkyc

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.regex.Matcher
import java.util.regex.Pattern

open class Helper {
    fun validateEditTextPanCard(text:String):Boolean{
        val pattern: Pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")

        val matcher: Matcher = pattern.matcher(text)

        return matcher.matches()


    }
    fun isCameraPermissionGranted(context:Context): Boolean {
        val cameraPermission = android.Manifest.permission.CAMERA
        val permissionStatus = ContextCompat.checkSelfPermission(context, cameraPermission)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    // Function to request camera permission
    fun requestCameraPermission(context:Activity,code:Int) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(android.Manifest.permission.CAMERA),
            code
        )
    }

    fun panCardEditTextListener(inputText: String, editText: EditText) {
//        editText.setText(inputText)
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val alphabetKeyboard = EditorInfo.IME_ACTION_NEXT // Use IME_ACTION_NEXT for alphabet keyboard
        val numberKeyboard = EditorInfo.IME_ACTION_DONE // Use IME_ACTION_DONE for number keyboard

        val length = inputText.length
        for (i in 0 until length) {
            val charType = when {
                i < 5 -> alphabetKeyboard
                i < 9 -> numberKeyboard
                else -> alphabetKeyboard
            }

            editText.imeOptions = charType
            if (i == length - 1) {
                // Show the keyboard for the last character
                editText.requestFocus()
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            } else {
                // Move focus to the next EditText
                editText.imeOptions = editText.imeOptions or EditorInfo.IME_FLAG_NO_ENTER_ACTION
                editText.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT)
                editText.nextFocusForwardId = editText.nextFocusForwardId + 1
            }

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val currentInputType = editText.inputType
                    val newInputType = when {
                        editText.text.length < 5 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                        editText.text.length < 9 -> InputType.TYPE_CLASS_NUMBER
                        else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    }

                    if (currentInputType != newInputType) {
                        editText.inputType = newInputType
                        editText.setSelection(editText.text.length) // Maintain cursor position
                    }
                }
            })

            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == charType) {
                    // Hide the keyboard when the action button is pressed
                    imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    true
                } else {
                    false
                }
            }
        }
    }

}