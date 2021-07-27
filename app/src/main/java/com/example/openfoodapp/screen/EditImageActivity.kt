package com.example.openfoodapp.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.example.openfoodapp.R
import com.theartofdev.edmodo.cropper.CropImage

class EditImageActivity : AppCompatActivity() {

    private lateinit var confirmCropImage : Button
    private var croppedImageUri : Uri? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(p0: Context, p1: Any?): Intent {
            return CropImage.activity().getIntent(this@EditImageActivity)
        }

        override fun parseResult(p0: Int, p1: Intent?): Uri? {
            confirmCropImage.visibility = View.VISIBLE
            return CropImage.getActivityResult(p1)?.uri
        }

    }
    private lateinit var cropActivityResultLaunch: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)
        val selectImage = findViewById<Button>(R.id.button_select_image)
        val cropedImage = findViewById<ImageView>(R.id.croped_image)
        confirmCropImage = findViewById(R.id.confirm_cropped_image)
        cropActivityResultLaunch = registerForActivityResult(cropActivityResultContract){ uri->
            croppedImageUri = uri
            cropedImage.setImageURI(uri)
        }

        selectImage.setOnClickListener {
            cropActivityResultLaunch.launch(null)
        }

        confirmCropImage.setOnClickListener {
            val sharedPref = this.getSharedPreferences("Sharedpref",Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString("croppedImageUri", croppedImageUri.toString())
                apply()
            }
            finish()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}